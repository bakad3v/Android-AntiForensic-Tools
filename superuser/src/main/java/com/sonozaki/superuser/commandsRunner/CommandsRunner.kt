package com.sonozaki.superuser.commandsRunner

import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.UserManager
import android.provider.Settings
import com.anggrayudi.storage.extension.toBoolean
import com.anggrayudi.storage.extension.toInt
import com.sonozaki.entities.ProfileDomain
import com.sonozaki.superuser.R
import com.sonozaki.superuser.mapper.ProfilesMapper
import com.sonozaki.superuser.superuser.SuperUser
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.utils.UIText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.BufferedSource
import okio.buffer
import okio.sink
import java.io.File

abstract class CommandsRunner(private val context: Context,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val profilesMapper: ProfilesMapper,
    private val userManager: UserManager,
    private val deviceAdmin: ComponentName): SuperUser {
    abstract suspend fun runCommand(command: String): CommandResult

    protected suspend fun checkAdminApp(packageName: String) {
        if (packageName == context.packageName) {
            for (profile in getProfiles()) {
                try {
                    runCommand("dpm remove-active-admin --user ${profile.id} ${context.packageName}/${deviceAdmin.shortClassName}")
                } catch (_: SuperUserException) {
                }
            }
        }
    }

    override suspend fun uninstallApp(packageName: String) {
        checkAdminApp(packageName)
        runCommand("pm uninstall $packageName")
    }

    override suspend fun hideApp(packageName: String) {
        checkAdminApp(packageName)
        runCommand("pm disable $packageName")
    }

    override suspend fun clearAppData(packageName: String) {
        checkAdminApp(packageName)
        runCommand("pm clear $packageName")
    }

    override suspend fun removeProfile(id: Int) {
        runCommand("pm remove-user $id")
    }

    override suspend fun stopLogd() {
        runCommand("stop logd")
    }

    override suspend fun setMultiuserUI(status: Boolean) {
        runCommand("setprop fw.show_multiuserui ${status.toInt()}")
    }

    override suspend fun setUsersLimit(limit: Int) {
        runCommand("setprop fw.max_users $limit")
    }

    override suspend fun getUserLimit(): Int? {
        return Regex("\\d").find(runCommand("pm get-max-users").output[0])?.value?.toInt()
    }

    override suspend fun setSafeBootStatus(status: Boolean) {
        runCommand("settings put global safe_boot_disallowed ${status.toInt()}")
    }

    override suspend fun setUserSwitcherStatus(status: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            throw SuperUserException(
                ANDROID_VERSION_INCORRECT.format(Build.VERSION_CODES.Q),
                UIText.StringResource(
                    R.string.wrong_android_version,
                    Build.VERSION_CODES.P.toString()
                )
            )
        runCommand("settings put global user_switcher_enabled ${status.toInt()}")
    }

    override suspend fun getUserSwitcherStatus(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            throw SuperUserException(
                ANDROID_VERSION_INCORRECT.format(Build.VERSION_CODES.Q),
                UIText.StringResource(
                    R.string.wrong_android_version,
                    Build.VERSION_CODES.P.toString()
                )
            )
        return try {
            runCommand("settings get global user_switcher_enabled").output[0].toInt()
                .toBoolean()
        } catch (e: NumberFormatException) {
            throw SuperUserException(
                NUMBER_NOT_RECOGNISED, UIText.StringResource(
                    com.sonozaki.resources.R.string.number_not_found
                )
            )
        }
    }

    override suspend fun setSwitchUserRestriction(status: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
            throw SuperUserException(
                ANDROID_VERSION_INCORRECT.format(Build.VERSION_CODES.P),
                UIText.StringResource(
                    R.string.wrong_android_version,
                    Build.VERSION_CODES.P.toString()
                )
            )
        runCommand("pm set-user-restriction ${UserManager.DISALLOW_USER_SWITCH} ${status.toInt()}")
    }

    override suspend fun getSwitchUserRestriction(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
            throw SuperUserException(
                ANDROID_VERSION_INCORRECT.format(Build.VERSION_CODES.P),
                UIText.StringResource(
                    R.string.wrong_android_version,
                    Build.VERSION_CODES.P.toString()
                )
            )
        return try {
            runCommand("dumpsys user | grep \"${UserManager.DISALLOW_USER_SWITCH}\"").output[0].endsWith(
                UserManager.DISALLOW_USER_SWITCH
            )
        } catch (e: SuperUserException) {
            false
        }
    }

    override suspend fun reboot() {
        runCommand("reboot")
    }

    override suspend fun openProfile(userId: Int) {
        runCommand("am switch-user $userId")
    }

    override suspend fun stopProfile(userId: Int, isCurrent: Boolean): Boolean {
        if (userId == 0) {
            throw SuperUserException(
                PRIMARY_USER_LOGOUT,
                UIText.StringResource(R.string.cant_logout_from_primary_user)
            )
        }
        if (isCurrent) {
            runCommand("am switch-user 0")
            return runCommand("am stop-user -w $userId").isSuccess
        }
        return runCommand("am stop-user -w $userId").isSuccess
    }

    override suspend fun installTestOnlyApp(length: Long, data: BufferedSource): Boolean =
        withContext(coroutineDispatcher) {
            val apkFile = File(context.filesDir, "base.apk")
            data.use { src -> apkFile.sink().buffer().use { it.writeAll(src); it.flush() } }
            try {
                return@withContext runCommand("pm install -t -r ${apkFile.absolutePath}").isSuccess
            } catch (e: SuperUserException) {
                return@withContext runCommand("cat \"${apkFile.absolutePath}\" | pm install -t -r -S $length").isSuccess
            }
        }

    override suspend fun getLogsStatus(): Boolean {
        val resultData = runCommand("getprop persist.log.tag").output
        if (resultData.isEmpty()) {
            throw SuperUserException(
                DATA_NOT_FOUND,
                UIText.StringResource(R.string.data_not_found)
            )
        }
        return !resultData.first().startsWith("S")
    }

    override suspend fun getDeveloperSettingsStatus(): Boolean {
        return try {
            runCommand("settings get global ${Settings.Global.DEVELOPMENT_SETTINGS_ENABLED}").output.first().toInt().toBoolean()
        } catch (e: NumberFormatException) {
            throw SuperUserException(
                NUMBER_NOT_RECOGNISED,
                UIText.StringResource(com.sonozaki.resources.R.string.number_not_found)
            )
        }
    }

    override suspend fun changeLogsStatus(enable: Boolean) {
        if (enable) {
            runCommand("setprop persist.log.tag \"\"")
        } else {
            runCommand("setprop persist.logd.logpersistd \"\"")
            runCommand("setprop persist.logd.logpersistd.buffer \"\"")
            runCommand("setprop logd.logpersistd \"\"")
            runCommand("setprop logd.logpersistd.buffer \"\"")
            runCommand("setprop persist.log.tag Settings")
            runCommand("setprop persist.logd.size 65536")
        }
    }

    override suspend fun changeDeveloperSettingsStatus(unlock: Boolean) {
        runCommand("settings put global ${Settings.Global.DEVELOPMENT_SETTINGS_ENABLED} ${unlock.toInt()}")
    }

    override suspend fun getSafeBootStatus(): Boolean {
        val result = runCommand("settings get global safe_boot_disallowed").output[0]
        if (result.endsWith("null")) {
            return false
        }
        try {
            return result.toInt().toBoolean()
        } catch (e: NumberFormatException) {
            throw SuperUserException(
                NUMBER_NOT_RECOGNISED,
                UIText.StringResource(com.sonozaki.resources.R.string.number_not_found)
            )
        }
    }


    override suspend fun getMultiuserUIStatus(): Boolean =
        try {
            runCommand("getprop fw.show_multiuserui").output[0].toInt().toBoolean()
        } catch (e: NumberFormatException) {
            throw SuperUserException(
                NUMBER_NOT_RECOGNISED, UIText.StringResource(
                    com.sonozaki.resources.R.string.number_not_found
                )
            )
        }

    override suspend fun wipe() {
        if (userManager.isSystemUser) {
            try {
                runCommand("am broadcast -a android.intent.action.MASTER_CLEAR -n android/com.android.server.MasterClearReceiver")
            } catch (e: SuperUserException) {
                runCommand("recovery --wipe_data")
            }
        } else {
            runCommand("am broadcast -a android.intent.action.MASTER_CLEAR -n android/com.android.server.MasterClearReceiver")
        }
    }

    override suspend fun getProfiles(): List<ProfileDomain> {
        val result = runCommand("pm list users")
        return result.output.drop(1).filter { it.isNotBlank() }.map { profilesMapper.mapRootOutputToProfile(it) }
    }

    override suspend fun runTrim() {
        runCommand("sm fstrim &")
    }

    override suspend fun removeNotification(packageName: String, id: Int) {
        runCommand("service call notification 2 s16 $packageName i32 $id")
    }

    companion object {
        const val ANDROID_VERSION_INCORRECT =
            "Wrong android version, SDK version %s or higher required"
        private const val PRIMARY_USER_LOGOUT = "You can't logout from primary user"
        private const val NUMBER_NOT_RECOGNISED = "Number not recognised"
        private const val DATA_NOT_FOUND = "Data not found"
    }
}