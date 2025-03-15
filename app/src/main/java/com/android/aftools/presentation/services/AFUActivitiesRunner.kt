package com.android.aftools.presentation.services

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.android.aftools.R
import com.android.aftools.di.DispatchersModule.Companion.IO_DISPATCHER
import com.android.aftools.domain.entities.FileDomain
import com.android.aftools.domain.entities.FileType
import com.android.aftools.domain.entities.Settings
import com.android.aftools.domain.usecases.filesDatabase.DeleteMyFileUseCase
import com.android.aftools.domain.usecases.filesDatabase.GetFilesDbUseCase
import com.android.aftools.domain.usecases.logs.GetLogsDataUseCase
import com.android.aftools.domain.usecases.logs.WriteToLogsUseCase
import com.android.aftools.domain.usecases.permissions.GetPermissionsUseCase
import com.android.aftools.domain.usecases.settings.GetSettingsUseCase
import com.android.aftools.presentation.utils.UIText
import com.android.aftools.superuser.superuser.SuperUser
import com.android.aftools.superuser.superuser.SuperUserException
import com.android.aftools.superuser.superuser.SuperUserManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Class for running tasks requiring phone's unlock safely
 */
@Singleton
class AFUActivitiesRunner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val writeToLogsUseCase: WriteToLogsUseCase,
    private val getFilesDbUseCase: GetFilesDbUseCase,
    private val deleteMyFileUseCase: DeleteMyFileUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val getPermissionsUseCase: GetPermissionsUseCase,
    private val superUserManager: SuperUserManager,
    private val getLogsDataUseCase: GetLogsDataUseCase,
    @Named(IO_DISPATCHER) private val ioDispatcher: CoroutineDispatcher
) {

    private val mutex = Mutex()
    private var isRunning = false
    private var logsAllowed: Boolean? = null

    suspend fun runTask() {
        mutex.withLock {
            if (isRunning) {
                return
            }
            isRunning = true
        }
        runAFUActivity()
        mutex.withLock {
            isRunning = false
        }

    }


    private suspend fun runAFUActivity() {
        val settings = getSettingsUseCase().first()
        if (!settings.deleteFiles && !settings.removeItself && !settings.hideItself && !settings.clearItself && !settings.clearData) {
            return
        } //getting settings
        try {
            logsAllowed = getLogsDataUseCase().first().logsEnabled
            writeToLogs(R.string.deletion_started) //getting log status, trying to write to logs
        } catch (e: Exception) {
            return
        }
        try {
            val files = getFilesDbUseCase().first()
            removeAll(files) //getting files, removing files
            writeToLogs(R.string.deletion_completed)
        } catch (e: Exception) {
            writeToLogs(R.string.getting_data_error, e.stackTraceToString())
        }
        val permissions = getPermissionsUseCase().first()
        if (!permissions.isRoot && !permissions.isOwner) {
            if (settings.clearData) {
                if (permissions.isAdmin) {
                    try {
                        superUserManager.removeAdminRights()
                    } catch (e: SuperUserException) {
                        writeToLogs(e.messageForLogs)
                    }
                }
                context.clearData(false) {

                }
            }
            return
        }
        val superUser = superUserManager.getSuperUser()
        if (permissions.isRoot) {
            if (settings.trim)
                runTrim(superUser,settings)
        }
        try {
            writeToLogs(R.string.uninstalling_itself)
            context.destroyApp(settings,superUser,permissions.isAdmin,superUserManager) {
                writeToLogs(R.string.uninstallation_failed, it)
            }
        } catch (e: Exception) {
            writeToLogs(R.string.uninstallation_failed, e.stackTraceToString())
        }
    }

    /**
     * Function for running TRIM
     */
    private suspend fun runTrim(superUser: SuperUser, settings: Settings) {
        if (settings.trim) {
            try {
                writeToLogs(R.string.running_trim)
                superUser.runTrim()
            } catch (e: SuperUserException) {
                writeToLogs(e.messageForLogs)
            } catch (e: Exception) {
                writeToLogs(R.string.trim_failed, e.stackTraceToString())
            }
            writeToLogs(R.string.trim_runned)
        }
    }

    private suspend fun writeToLogs(resource: UIText.StringResource) {
        if (logsAllowed == true)
            writeToLogsUseCase(resource.asString(context))
    }

    private suspend fun writeToLogs(rId: Int, vararg obj: String) {
        if (logsAllowed == true)
            writeToLogsUseCase(context.getString(rId, *obj))
    }

    private fun FileDomain.toDocumentFile(): DocumentFile? {
        return if (fileType == FileType.DIRECTORY) {
            DocumentFile.fromTreeUri(context, uri)
        } else {
            DocumentFile.fromSingleUri(context, uri)
        }
    }

    /**
     * Removing all files
     */
    private suspend fun removeAll(filesList: List<FileDomain>) {
        coroutineScope {
            filesList.sortedByDescending { it.priority }.groupBy { it.priority }.forEach { it1 ->
                val jobs: List<Job> = it1.value.map {
                    removeFile(this, it)
                }
                jobs.joinAll()
            } //sorting and grouping files by priority
        }
    }

    /**
     * Preprocessing and carrying out file or folder removal and analyzing results
     */
    private fun removeFile(coroutineScope: CoroutineScope, file: FileDomain): Job {
        return coroutineScope.launch(ioDispatcher) {
            val name = file.name
            val isDirectory = file.fileType == FileType.DIRECTORY
            val id = if (isDirectory) {
                R.string.deletion_folder
            } else {
                R.string.deletion_file
            }
            writeToLogs(id, name)
            val df = try {
                file.toDocumentFile() ?: throw RuntimeException()
            } catch (e: Exception) {
                writeAboutDeletionError(isDirectory, name, context.getString(R.string.access_error))
                return@launch
            }
            val result: Pair<Int, Int> = deleteFile(df, file.name, isDirectory)
            processDeletionResults(result, isDirectory, file)
        }
    }

    /**
     * Deleting file or folder
     */
    private suspend fun deleteFile(
        df: DocumentFile,
        path: String,
        isDirectory: Boolean
    ): Pair<Int, Int> {
        if (isDirectory) {
            val resultFiles = mutableListOf<Pair<Int, Int>>()
            val resultDirs = mutableListOf<Deferred<Pair<Int, Int>>>()
            df.listFiles().forEach {
                if (it.isDirectory) {
                    resultDirs += coroutineScope {
                        async(ioDispatcher) {
                            deleteFile(
                                it,
                                it.name ?: "Unknown",
                                true
                            )
                        }
                    }
                } else {
                    resultFiles += deleteFile(it, it.name ?: "Unknown", false)
                }
            }
            val result = resultFiles + resultDirs.awaitAll()
            var (success, all) = listOf(0, 0)
            result.forEach { success += it.first; all += it.second }
            if (all == 0 || success / all > 0.5) {
                if (!df.delete()) {
                    writeAboutDeletionError(
                        true, path,
                        "File not deleted"
                    )
                }
            }
            return Pair(success, all)
        }
        if (!df.delete()) {
            writeAboutDeletionError(
                false, path,
                "Directory not deleted"
            )
            return Pair(0, 1)
        }
        return Pair(1, 1)
    }

    /**
     * Writing about deletion errors
     */
    private suspend fun writeAboutDeletionError(isDirectory: Boolean, name: String, error: String) {
        val id1 = if (isDirectory) {
            R.string.folder_deletion_error
        } else {
            R.string.file_deletion_error
        }
        writeToLogs(id1, name, error)
    }

    /**
     * Processing results of file deletion and writing to logs
     */
    private suspend fun processDeletionResults(
        result: Pair<Int, Int>,
        isDirectory: Boolean,
        file: FileDomain
    ) {
        if (isDirectory) {
            if (result.second == 0) {
                deleteMyFileUseCase(file.uri)
                writeToLogs(
                    R.string.folder_deletion_success,
                    file.name,
                    "100"
                )
                return
            }
            val percent = result.first / result.second
            if (percent > 0.5) {
                deleteMyFileUseCase(file.uri)
                writeToLogs(
                    R.string.folder_deletion_success,
                    file.name,
                    (percent * 100).toString()
                )
                return
            }
            writeToLogs(
                R.string.folder_deletion_failed,
                file.name,
                (percent * 100).toString()
            )
            return
        }
        if (result.first == 1) {
            deleteMyFileUseCase(file.uri)
            writeToLogs(
                R.string.deletion_success,
                file.name
            )
            return
        }
        writeToLogs(
            R.string.deletion_failed,
            file.name
        )
    }
}