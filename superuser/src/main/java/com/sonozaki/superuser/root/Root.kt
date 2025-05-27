package com.sonozaki.superuser.root

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.UserManager
import android.util.Log
import com.anggrayudi.storage.extension.toBoolean
import com.anggrayudi.storage.extension.toInt
import com.sonozaki.entities.ProfileDomain
import com.sonozaki.resources.IO_DISPATCHER
import com.sonozaki.superuser.R
import com.sonozaki.superuser.domain.usecases.SetRootInactiveUseCase
import com.sonozaki.superuser.mapper.ProfilesMapper
import com.sonozaki.superuser.superuser.SuperUser
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.utils.UIText
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.BufferedSource
import okio.buffer
import okio.sink
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class Root @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profilesMapper: ProfilesMapper,
    private val setRootInactiveUseCase: SetRootInactiveUseCase,
    private val dpm: DevicePolicyManager,
    private val userManager: UserManager,
    private val deviceAdmin: ComponentName,
    @Named(IO_DISPATCHER) private val coroutineDispatcher: CoroutineDispatcher
) : SuperUser {

    override suspend fun executeRootCommand(command: String): Shell.Result {
        val result = Shell.cmd(command).exec()
        if (!result.isSuccess) {
            if (!askSuperUserRights()) {
                setRootInactiveUseCase()
                throw SuperUserException(
                    NO_ROOT_RIGHTS,
                    UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
                )
            }
            val resultText = result.out.joinToString(";")
            throw SuperUserException(
                resultText,
                UIText.StringResource(R.string.unknow_root_error, resultText)
            )
        }
        return result
    }

    private suspend fun checkAdminApp(packageName: String) {
        if (packageName == context.packageName) {
            for (profile in getProfiles()) {
                try {
                    executeRootCommand("dpm remove-active-admin --user ${profile.id} ${context.packageName}/${deviceAdmin.shortClassName}")
                } catch (_: SuperUserException) {
                }
            }
        }
    }

    override suspend fun uninstallApp(packageName: String) {
        checkAdminApp(packageName)
        executeRootCommand("pm uninstall $packageName")
    }

    override suspend fun hideApp(packageName: String) {
        checkAdminApp(packageName)
        executeRootCommand("pm disable $packageName")
    }

    override suspend fun clearAppData(packageName: String) {
        checkAdminApp(packageName)
        executeRootCommand("pm clear $packageName")
    }

    override suspend fun removeProfile(id: Int) {
        executeRootCommand("pm remove-user $id")
    }

    override suspend fun stopLogd() {
        executeRootCommand("stop logd")
    }

    override suspend fun setMultiuserUI(status: Boolean) {
        executeRootCommand("setprop fw.show_multiuserui ${status.toInt()}")
    }

    override suspend fun setUsersLimit(limit: Int) {
        executeRootCommand("setprop fw.max_users $limit")
    }

    override suspend fun getUserLimit(): Int? {
        return Regex("\\d").find(executeRootCommand("pm get-max-users").out[0])?.value?.toInt()
    }

    override suspend fun setSafeBootStatus(status: Boolean) {
        executeRootCommand("pm set-user-restriction ${UserManager.DISALLOW_SAFE_BOOT} ${status.toInt()}")
        executeRootCommand("settings put global safe_boot_disallowed ${status.toInt()}")
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
        executeRootCommand("settings put global user_switcher_enabled ${status.toInt()}")
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
            executeRootCommand("settings get global user_switcher_enabled").out[0].toInt()
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
        executeRootCommand("pm set-user-restriction ${UserManager.DISALLOW_USER_SWITCH} ${status.toInt()}")
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
            executeRootCommand("dumpsys user | grep \"${UserManager.DISALLOW_USER_SWITCH}\"").out[0].endsWith(
                UserManager.DISALLOW_USER_SWITCH
            )
        } catch (e: SuperUserException) {
            false
        }
    }

    override suspend fun reboot() {
        executeRootCommand("reboot")
    }

    override suspend fun stopProfile(userId: Int, isCurrent: Boolean): Boolean {
        if (userId == 0) {
            throw SuperUserException(
                PRIMARY_USER_LOGOUT,
                UIText.StringResource(R.string.cant_logout_from_primary_user)
            )
        }
        if (isCurrent) {
            executeRootCommand("am switch-user 0")
            return executeRootCommand("am stop-user -w $userId").isSuccess
        }
        return executeRootCommand("am stop-user -w $userId").isSuccess
    }

    override suspend fun installTestOnlyApp(length: Long, data: BufferedSource): Boolean =
        withContext(coroutineDispatcher) {
            val apkFile = File(context.filesDir, "base.apk")
            data.use { src -> apkFile.sink().buffer().use { it.writeAll(src); it.flush() } }
            try {
                return@withContext executeRootCommand("pm install -t -r ${apkFile.absolutePath}").isSuccess
            } catch (e: SuperUserException) {
                return@withContext executeRootCommand("cat \"${apkFile.absolutePath}\" | pm install -t -r -S $length").isSuccess
            }
        }


    override suspend fun getSafeBootStatus(): Boolean {
        val result = executeRootCommand("settings get global safe_boot_disallowed").out[0]
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
            executeRootCommand("getprop fw.show_multiuserui").out[0].toInt().toBoolean()
        } catch (e: NumberFormatException) {
            throw SuperUserException(
                NUMBER_NOT_RECOGNISED, UIText.StringResource(
                    com.sonozaki.resources.R.string.number_not_found
                )
            )
        }

    fun askSuperUserRights(): Boolean {
        val result = Shell.cmd("id").exec()
        return result.out[0].startsWith("uid=0(root)")
    }

    override suspend fun wipe() {
        if (userManager.isSystemUser) {
            executeRootCommand("recovery --wipe_data")
        } else {
            executeRootCommand("am broadcast -a android.intent.action.MASTER_CLEAR -n android/com.android.server.MasterClearReceiver")
        }
    }

    override suspend fun getProfiles(): List<ProfileDomain> {
        val result = executeRootCommand("pm list users")
        return result.out.drop(1).map { profilesMapper.mapRootOutputToProfile(it) }
    }

    override suspend fun runTrim() {
        Shell.cmd("sm fstrim &").submit()
    }

    companion object {
        private const val NO_ROOT_RIGHTS = "App doesn't have root rights"
        private const val ANDROID_VERSION_INCORRECT =
            "Wrong android version, SDK version %s or higher required"
        private const val PRIMARY_USER_LOGOUT = "You can't logout from primary user"
        private const val NUMBER_NOT_RECOGNISED = "Number not recognised"
    }
}