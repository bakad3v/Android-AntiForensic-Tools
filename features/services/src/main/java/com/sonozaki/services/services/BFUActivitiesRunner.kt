package com.sonozaki.services.services

import android.content.Context
import com.sonozaki.services.R
import com.sonozaki.services.domain.usecases.GetLogsDataUseCase
import com.sonozaki.services.domain.usecases.GetManagedAppsUseCase
import com.sonozaki.services.domain.usecases.GetProfilesToDeleteUseCase
import com.sonozaki.services.domain.usecases.GetRootCommandUseCase
import com.sonozaki.services.domain.usecases.GetSettingsUseCase
import com.sonozaki.services.domain.usecases.RemoveApplicationUseCase
import com.sonozaki.services.domain.usecases.RemoveProfileFromDeletionUseCase
import com.sonozaki.services.domain.usecases.WriteToLogsUseCase
import com.sonozaki.superuser.domain.usecases.GetPermissionsUseCase
import com.sonozaki.superuser.superuser.SuperUser
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.superuser.superuser.SuperUserManager
import com.sonozaki.utils.UIText
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class for running tasks that can be completed before the device is unlocked
 */
@Singleton
class BFUActivitiesRunner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val getProfilesToDelete: GetProfilesToDeleteUseCase,
    private val getManagedAppsUseCase: GetManagedAppsUseCase,
    private val removeApplicationUseCase: RemoveApplicationUseCase,
    private val writeToLogsUseCase: WriteToLogsUseCase,
    private val superUserManager: SuperUserManager,
    private val removeProfileFromDeletionUseCase: RemoveProfileFromDeletionUseCase,
    private val getPermissionsUseCase: GetPermissionsUseCase,
    private val getLogsDataUseCase: GetLogsDataUseCase,
    private val getRootCommandUseCase: GetRootCommandUseCase
) {

    private var logsAllowed: Boolean? = null
    private val mutex = Mutex()
    private var isRunning = false

    suspend fun runTask() {
        mutex.withLock {
            if (isRunning) {
                return
            }
            isRunning = true
        }
        runBFUActivity()
        mutex.withLock {
            isRunning = false
        }
    }

    private suspend fun writeToLogs(rId: Int, vararg obj: String) {
        if (logsAllowed == true)
            writeToLogsUseCase(context.getString(rId, *obj))
    }

    private suspend fun writeToLogs(text: UIText.StringResource) {
        if (logsAllowed == true)
            writeToLogsUseCase(text.asString(context))
    }

    private suspend fun writeToLogs(text: String) {
        if (logsAllowed == true)
            writeToLogsUseCase(text)
    }

    /**
     * Function to run superuser actions, check results and write progress to logs.
     * @param startText Text to write to logs on start of the action
     * @param successText Text to write to logs on success
     * @param failureText Text to write to logs on error
     * @param startTextParams Parameters of startText
     * @param action Action to run
     * @return true in case of success, false in case of failure
     */
    private suspend fun runSuperuserAction(
        startText: Int,
        successText: Int,
        failureText: Int,
        vararg startTextParams: String,
        action: suspend () -> Unit
    ): Boolean {
        try {
            writeToLogs(startText, *startTextParams)
            action()
            writeToLogs(successText)
            return true
        } catch (e: SuperUserException) {
            writeToLogs(e.messageForLogs)
        } catch (e: Exception) {
            writeToLogs(failureText, e.stackTraceToString())
        }
        return false
    }

    /**
     * Function for removing all profiles marked for deletion and deleting them from list of profiles
     */
    private suspend fun removeProfiles(superUser: SuperUser) {
        writeToLogs(R.string.getting_profiles)
        val profiles = try {
            getProfilesToDelete()
        } catch (e: Exception) {
            writeToLogs(R.string.getting_profiles_failed)
            return
        }
        profiles.forEach {
            runSuperuserAction(
                R.string.removing_profile,
                R.string.profile_removed,
                R.string.profile_not_removed,
                it.toString()
            ) {
                superUser.removeProfile(it)
            }
        }
        profiles.forEach {
            removeProfileFromDeletionUseCase(it)
        }
    }

    /**
     * Function for removing all apps marked for deletion and deleting them from list of apps
     */
    private suspend fun deleteApps(
        superUser: SuperUser
    ) {
        val apps = getManagedAppsUseCase()
        apps.forEach {
            if (it.toDelete) {
                runSuperuserAction(
                    R.string.uninstalling_app,
                    R.string.app_uninstalled,
                    R.string.app_not_uninstalled,
                    it.packageName
                ) {
                    superUser.uninstallApp(it.packageName)
                }
            }
            if (it.toClearData) {
                runSuperuserAction(
                    R.string.clearing_app_data,
                    R.string.app_data_cleared,
                    R.string.app_data_not_cleared,
                    it.packageName
                ) {
                    superUser.clearAppData(it.packageName)
                }
            }
            if (it.toHide) {
                runSuperuserAction(
                    R.string.hiding_app,
                    R.string.app_hidden,
                    R.string.app_not_hidden,
                    it.packageName
                ) {
                    superUser.hideApp(it.packageName)
                }
            }
        }
        apps.forEach {
            removeApplicationUseCase(it.packageName)
        }
    }

    private suspend fun runRootCommand(superUser: SuperUser, command: String) {
        writeToLogs(UIText.StringResource(R.string.running_root_command, command))
        try {
            val result = superUser.executeRootCommand(command)
            result.out.forEach {
                writeToLogs(it)
            }
        } catch (e: SuperUserException) {
            writeToLogs(e.messageForLogs)
        } catch (e: Exception) {
            writeToLogs(R.string.root_command_failed, e.stackTraceToString())
        }
    }


    private suspend fun runBFUActivity() {
        try {
            logsAllowed = getLogsDataUseCase().logsEnabled
            writeToLogs(R.string.actions_started)
        } catch (e: Exception) {
            return
        }
        writeToLogs(R.string.loading_data)
        val (permissions, settings) = try {
            Pair(
                getPermissionsUseCase(),
                getSettingsUseCase()
            )
        } catch (e: Exception) {
            writeToLogs(R.string.getting_data_error, e.stackTraceToString())
            return
        }
        writeToLogs(R.string.got_data)
        if (!permissions.isRoot && !permissions.isOwner && !permissions.isAdmin) {
            if (!settings.deleteFiles && settings.clearData) {
                context.clearData(false) {
                    writeToLogs(it)
                }
            }
            return
        }
        val superUser = superUserManager.getSuperUser()
        if (settings.wipe) {
            runSuperuserAction(
                R.string.wiping_data,
                R.string.data_wiped,
                R.string.wiping_data_error
            ) {
                superUser.wipe()
            }
            return
        }
        if (!permissions.isRoot && !permissions.isOwner) {
            if (!settings.deleteFiles && settings.clearData)  {
                try {
                    superUserManager.removeAdminRights()
                } catch (e: SuperUserException) {
                    writeToLogs(e.messageForLogs)
                }
                context.clearData(false) {
                    writeToLogs(it)
                }
            }
            return
        }
        if (permissions.isRoot && settings.stopLogdOnStart) {
            runSuperuserAction(
                R.string.stopping_logd,
                R.string.logd_stopped,
                R.string.logd_failed
            ) {
                superUser.stopLogd()
            }
        }
        if (settings.deleteProfiles) {
            removeProfiles(superUser)
        }
        if (settings.deleteApps) {
            deleteApps(superUser)
        }
        if (permissions.isRoot) {
            if (settings.runRoot) {
                getRootCommandUseCase().split("\n").forEach {
                    runRootCommand(superUser, it)
                }
            }
        }
        if (settings.deleteFiles) {
            return
        } //if there are some files marked for deletion, the following actions must be postponed until the removal of files is completed
        if (permissions.isRoot) {
            if (settings.trim) {
                runSuperuserAction(
                    R.string.running_trim,
                    R.string.trim_runned,
                    R.string.trim_failed
                ) {
                    superUser.runTrim()
                }
            }
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
}