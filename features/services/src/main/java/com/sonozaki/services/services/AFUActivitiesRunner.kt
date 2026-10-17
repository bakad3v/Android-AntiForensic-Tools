package com.sonozaki.services.services

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.sonozaki.entities.FileType
import com.sonozaki.resources.IO_DISPATCHER
import com.sonozaki.services.R
import com.sonozaki.services.domain.entities.FileDomain
import com.sonozaki.services.domain.usecases.DeleteMyFileUseCase
import com.sonozaki.services.domain.usecases.GetFilesUseCase
import com.sonozaki.services.domain.usecases.GetLogsDataUseCase
import com.sonozaki.services.domain.usecases.GetSettingsUseCase
import com.sonozaki.services.domain.usecases.WriteToLogsUseCase
import com.sonozaki.superuser.domain.usecases.GetPermissionsUseCase
import com.sonozaki.superuser.superuser.SuperUser
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.superuser.superuser.SuperUserManager
import com.sonozaki.utils.UIText
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Named

/**
 * Class for running tasks requiring phone's unlock safely
 */
class AFUActivitiesRunner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val writeToLogsUseCase: WriteToLogsUseCase,
    private val getFilesUseCase: GetFilesUseCase,
    private val deleteMyFileUseCase: DeleteMyFileUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val getPermissionsUseCase: GetPermissionsUseCase,
    private val superUserManager: SuperUserManager,
    private val getLogsDataUseCase: GetLogsDataUseCase,
    @Named(IO_DISPATCHER) private val ioDispatcher: CoroutineDispatcher
): ActivityRunner {

    private val mutex = Mutex()
    private var logsAllowed: Boolean? = null

    override suspend fun runTask(): Boolean {
        return mutex.withLock {
            logsAllowed = null
            try {
                runAFUActivity()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                writeToLogs(R.string.getting_data_error, e.stackTraceToString())
                false
            }
        }
    }

    /** Logging is best-effort and must not cancel file deletion. */
    private suspend fun writeToLogs(action: suspend () -> Unit) {
        if (logsAllowed != true) return
        try {
            action()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // Ignore log storage failures and keep running the requested action.
        }
    }


    private suspend fun runAFUActivity(): Boolean {
        val settings = getSettingsUseCase()
        if (!settings.deleteFiles && !settings.removeItself && !settings.hideItself &&
            !settings.clearItself && !settings.clearData && !settings.trim
        ) {
            return true
        } //getting settings
        logsAllowed = try {
            getLogsDataUseCase().logsEnabled
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            false
        }
        writeToLogs(R.string.deletion_started)
        var completedSuccessfully = true
        if (settings.deleteFiles) {
            try {
                val files = getFilesUseCase()
                completedSuccessfully = removeAll(files) //getting files, removing files
                if (completedSuccessfully) {
                    writeToLogs(R.string.deletion_completed)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                completedSuccessfully = false
                writeToLogs(R.string.getting_data_error, e.stackTraceToString())
            }
        }
        val permissions = getPermissionsUseCase()
        if (!permissions.isRoot && !permissions.isOwner && !permissions.isShizuku) {
            if (settings.clearData) {
                if (permissions.isAdmin) {
                    try {
                        superUserManager.removeAdminRights()
                    } catch (e: SuperUserException) {
                        completedSuccessfully = false
                        writeToLogs(e.messageForLogs)
                    }
                }
                context.clearData(false) {
                    completedSuccessfully = false
                    writeToLogs(R.string.uninstallation_failed, it)
                }
            }
            if (settings.removeItself || settings.hideItself || settings.clearItself || settings.trim) {
                completedSuccessfully = false
            }
            return completedSuccessfully
        }
        val superUser = superUserManager.getSuperUser()
        if (settings.trim) {
            completedSuccessfully = if (permissions.isRoot || permissions.isShizuku) {
                runTrim(superUser) && completedSuccessfully
            } else {
                false
            }
        }
        try {
            writeToLogs(R.string.uninstalling_itself)
            context.destroyApp(settings,superUser,permissions.isAdmin,superUserManager) {
                completedSuccessfully = false
                writeToLogs(R.string.uninstallation_failed, it)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            completedSuccessfully = false
            writeToLogs(R.string.uninstallation_failed, e.stackTraceToString())
        }
        return completedSuccessfully
    }

    /**
     * Function for running TRIM
     */
    private suspend fun runTrim(superUser: SuperUser): Boolean {
        return try {
            writeToLogs(R.string.running_trim)
            superUser.runTrim()
            writeToLogs(R.string.trim_runned)
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: SuperUserException) {
            writeToLogs(e.messageForLogs)
            false
        } catch (e: Exception) {
            writeToLogs(R.string.trim_failed, e.stackTraceToString())
            false
        }
    }

    private suspend fun writeToLogs(resource: UIText.StringResource) {
        writeToLogs {
            writeToLogsUseCase(resource.asString(context))
        }
    }

    private suspend fun writeToLogs(rId: Int, vararg obj: String) {
        writeToLogs {
            writeToLogsUseCase(context.getString(rId, *obj))
        }
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
    private suspend fun removeAll(filesList: List<FileDomain>): Boolean {
        return coroutineScope {
            var completedSuccessfully = true
            filesList.sortedByDescending { it.priority }.groupBy { it.priority }.forEach { it1 ->
                val results: List<Deferred<Boolean>> = it1.value.map {
                    removeFile(this, it)
                }
                if (!results.awaitAll().all { it }) {
                    completedSuccessfully = false
                }
            } //sorting and grouping files by priority
            completedSuccessfully
        }
    }

    /**
     * Preprocessing and carrying out file or folder removal and analyzing results
     */
    private fun removeFile(coroutineScope: CoroutineScope, file: FileDomain): Deferred<Boolean> {
        return coroutineScope.async(ioDispatcher) {
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
                return@async false
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
            if (all == 0 || success.toFloat() / all > 0.5) {
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
    ): Boolean {
        if (isDirectory) {
            if (result.second == 0) {
                deleteMyFileUseCase(file.uri)
                writeToLogs(
                    R.string.folder_deletion_success,
                    file.name,
                    "100"
                )
                return true
            }
            val percent = result.first.toFloat() / result.second
            if (percent > 0.5) {
                deleteMyFileUseCase(file.uri)
                writeToLogs(
                    R.string.folder_deletion_success,
                    file.name,
                    (percent * 100).toString()
                )
                return true
            }
            writeToLogs(
                R.string.folder_deletion_failed,
                file.name,
                (percent * 100).toString()
            )
            return false
        }
        if (result.first == 1) {
            deleteMyFileUseCase(file.uri)
            writeToLogs(
                R.string.deletion_success,
                file.name
            )
            return true
        }
        writeToLogs(
            R.string.deletion_failed,
            file.name
        )
        return false
    }
}
