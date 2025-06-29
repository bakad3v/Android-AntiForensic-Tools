package com.sonozaki.superuser.owner

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.app.admin.IDevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.IPackageInstaller
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import android.os.UserManager
import com.rosan.dhizuku.api.Dhizuku
import com.rosan.dhizuku.api.Dhizuku.binderWrapper
import com.rosan.dhizuku.api.DhizukuBinderWrapper
import com.rosan.dhizuku.api.DhizukuRequestPermissionListener
import com.sonozaki.entities.ProfileDomain
import com.sonozaki.resources.IO_DISPATCHER
import com.sonozaki.superuser.R
import com.sonozaki.superuser.domain.usecases.SetOwnerInactiveUseCase
import com.sonozaki.superuser.mapper.ProfilesMapper
import com.sonozaki.superuser.superuser.SuperUser
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.utils.UIText
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.BufferedSource
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class DhizukuManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profilesMapper: ProfilesMapper,
    private val setOwnerInactiveUseCase: SetOwnerInactiveUseCase,
    private val appDPM: DevicePolicyManager,
    private val userManager: UserManager,
    private val deviceAdmin: ComponentName,
    @Named(IO_DISPATCHER) private val coroutineDispatcher: CoroutineDispatcher
) :
    SuperUser {

    private var initialized: Boolean = false
    private val dpm by lazy { getDhizukuDPM() }
    private val packageInstaller by lazy { getDhizukuPackageInstaller() }
    private val deviceOwner by lazy { Dhizuku.getOwnerComponent() }

    private fun initDhizuku() {
        Dhizuku.init(context)
        if (VERSION.SDK_INT >= Build.VERSION_CODES.P) HiddenApiBypass.setHiddenApiExemptions("")
        initialized = true
    }

    /**
     * Function for getting Dhizuku DPM. Thanks BinTianqi for this code.
     */
    @SuppressLint("PrivateApi", "SoonBlockedPrivateApi")
    private fun getDhizukuDPM(): DevicePolicyManager {
        val dhizukuContext = getDhizukuContext()
        DevicePolicyManager.DELEGATION_BLOCK_UNINSTALL
        val manager =
            dhizukuContext.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val field = manager.javaClass.getDeclaredField("mService")
        field.isAccessible = true
        val oldInterface = field[manager] as IDevicePolicyManager
        if (oldInterface is DhizukuBinderWrapper) return manager
        val oldBinder = oldInterface.asBinder()
        val newBinder = binderWrapper(oldBinder)
        val newInterface = IDevicePolicyManager.Stub.asInterface(newBinder)
        field[manager] = newInterface
        return manager
    }

    /**
     * Function for getting Dhizuku package installer. Thanks BinTianqi for this code.
     */
    @SuppressLint("SoonBlockedPrivateApi")
    private fun getDhizukuPackageInstaller(): PackageInstaller {
        val context = getDhizukuContext()
        val installer = context.packageManager.packageInstaller
        val field = installer.javaClass.getDeclaredField("mInstaller")
        field.isAccessible = true
        val oldInterface = field[installer] as IPackageInstaller
        if (oldInterface is DhizukuBinderWrapper) return installer
        val oldBinder = oldInterface.asBinder()
        val newBinder = binderWrapper(oldBinder)
        val newInterface = IPackageInstaller.Stub.asInterface(newBinder)
        field[installer] = newInterface
        return installer
    }

    private fun getDhizukuContext(): Context {
        if (!initialized) {
            initDhizuku()
        }
        return context.createPackageContext(
            Dhizuku.getOwnerComponent().packageName,
            Context.CONTEXT_IGNORE_SECURITY
        )
    }

    private fun checkAdminApp(packageName: String) {
        if (packageName == context.packageName && appDPM.isAdminActive(deviceAdmin)) {
            try {
                appDPM.removeActiveAdmin(deviceAdmin)
            } catch (e: Exception) {

            }
        }
    }


    private suspend fun handleException(e: Exception): Nothing {
        if (!checkOwner()) {
            setOwnerInactiveUseCase()
            throw SuperUserException(
                NO_OWNER_RIGHTS,
                UIText.StringResource(R.string.no_admin_rights)
            )
        }
        throw SuperUserException(
            e.stackTraceToString(),
            UIText.StringResource(R.string.unknow_owner_error, e.stackTraceToString())
        )
    }


    fun askSuperUserRights(onApprove: () -> Unit, onDeny: () -> Unit, onAbsent: () -> Unit) {
        if (!initialized) {
            initDhizuku()
        }
        try {
            Dhizuku.requestPermission(object : DhizukuRequestPermissionListener() {
                override fun onRequestPermission(grantResult: Int) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        onApprove()
                    } else {
                        onDeny()
                    }
                }
            })
        } catch (e: Exception) {
            onAbsent()
        }
    }

    private fun checkOwner(): Boolean {
        return dpm.isDeviceOwnerApp("com.rosan.dhizuku") && dpm.isAdminActive(deviceOwner)
    }


    override suspend fun wipe() {
        var flags = 0
        if (VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            flags = flags.or(DevicePolicyManager.WIPE_SILENTLY)
        try {
            if (userManager.isSystemUser && VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                dpm.wipeDevice(flags)
            } else {
                dpm.wipeData(flags)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun getProfiles(): List<ProfileDomain> = withContext(coroutineDispatcher) {
        try {
            val userHandles =
                if (VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    dpm.getSecondaryUsers(deviceOwner) ?: listOf()
                } else {
                    throw SuperUserException(
                        ANDROID_VERSION_INCORRECT.format(Build.VERSION_CODES.P),
                        UIText.StringResource(
                            R.string.wrong_android_version,
                            Build.VERSION_CODES.P.toString()
                        )
                    )
                }
            return@withContext userHandles.map { profilesMapper.mapUserHandleToProfile(it) }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun removeProfile(id: Int) {
        withContext(coroutineDispatcher) {
            try {
                dpm.removeUser(deviceOwner, profilesMapper.mapIdToUserHandle(id))
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    override suspend fun uninstallApp(packageName: String) = withContext(coroutineDispatcher) {
        throw SuperUserException(NOT_ENOUGH_RIGHTS, UIText.StringResource(R.string.not_enough_rights))
    }

    override suspend fun hideApp(packageName: String) {
        withContext(coroutineDispatcher) {
            try {
                checkAdminApp(packageName)
                dpm.setApplicationHidden(deviceOwner, packageName, true)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    override suspend fun clearAppData(packageName: String) {
        try {
            checkAdminApp(packageName)
            if (VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                dpm.clearApplicationUserData(
                    deviceOwner,
                    packageName,
                    Executors.newSingleThreadExecutor()
                ) { packae, success -> }
            } else {
                throw SuperUserException(
                    ANDROID_VERSION_INCORRECT.format(Build.VERSION_CODES.P),
                    UIText.StringResource(
                        R.string.wrong_android_version,
                        Build.VERSION_CODES.P.toString()
                    )
                )
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun setSafeBootStatus(status: Boolean) = withContext(coroutineDispatcher) {
        if (status) {
            dpm.addUserRestriction(deviceOwner, UserManager.DISALLOW_SAFE_BOOT)
        } else {
            dpm.clearUserRestriction(deviceOwner, UserManager.DISALLOW_SAFE_BOOT)
        }
    }

    override suspend fun getSafeBootStatus(): Boolean = withContext(coroutineDispatcher) {
        dpm.getUserRestrictions(deviceOwner).getBoolean(UserManager.DISALLOW_SAFE_BOOT)
    }

    override suspend fun setSwitchUserRestriction(status: Boolean) = withContext(coroutineDispatcher) {
        if (VERSION.SDK_INT < Build.VERSION_CODES.P)
            throw SuperUserException(
                ANDROID_VERSION_INCORRECT.format(Build.VERSION_CODES.P),
                UIText.StringResource(
                    R.string.wrong_android_version,
                    Build.VERSION_CODES.P.toString()
                )
            )
        if (status)
            dpm.addUserRestriction(deviceOwner, UserManager.DISALLOW_USER_SWITCH)
        else
            dpm.clearUserRestriction(deviceOwner, UserManager.DISALLOW_USER_SWITCH)
    }

    override suspend fun getSwitchUserRestriction(): Boolean = withContext(coroutineDispatcher) {
        if (VERSION.SDK_INT < Build.VERSION_CODES.P)
            throw SuperUserException(
                ANDROID_VERSION_INCORRECT.format(Build.VERSION_CODES.P),
                UIText.StringResource(
                    R.string.wrong_android_version,
                    Build.VERSION_CODES.P.toString()
                )
            )
        return@withContext dpm.getUserRestrictions(deviceOwner).getBoolean(UserManager.DISALLOW_USER_SWITCH)
    }

    override suspend fun reboot() {
        dpm.reboot(deviceOwner)
    }


    override suspend fun stopProfile(userId: Int, isCurrent: Boolean): Boolean {
        if (userId == 0) {
            throw SuperUserException(PRIMARY_USER_LOGOUT,
                UIText.StringResource(R.string.cant_logout_from_primary_user))
        }
        if (VERSION.SDK_INT < Build.VERSION_CODES.P) {
            throw SuperUserException(
                ANDROID_VERSION_INCORRECT.format(Build.VERSION_CODES.P),
                UIText.StringResource(
                    R.string.wrong_android_version,
                    Build.VERSION_CODES.P.toString()
                )
            )
        }
        if (isCurrent) {
            return dpm.logoutUser(deviceOwner) == UserManager.USER_OPERATION_SUCCESS
        }
        return dpm.stopUser(deviceOwner, profilesMapper.mapIdToUserHandle(userId)) == UserManager.USER_OPERATION_SUCCESS
    }

    override suspend fun installTestOnlyApp(length: Long, data: BufferedSource): Boolean = withContext(coroutineDispatcher) {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }

    override suspend fun changeLogsStatus(enable: Boolean) {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }

    override suspend fun changeDeveloperSettingsStatus(unlock: Boolean) {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }

    override suspend fun getLogsStatus(): Boolean {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }

    override suspend fun getDeveloperSettingsStatus(): Boolean {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }

    override suspend fun runTrim() {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }

    override suspend fun executeRootCommand(command: String): Shell.Result {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }

    override suspend fun stopLogd() {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }

    override fun getPowerButtonClicks(callback: (Boolean) -> Unit): () -> Unit {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }

    override suspend fun setMultiuserUI(status: Boolean) {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }

    override suspend fun setUsersLimit(limit: Int) {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }

    override suspend fun getUserLimit(): Int? {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }

    override suspend fun getMultiuserUIStatus(): Boolean {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }

    override suspend fun setUserSwitcherStatus(status: Boolean) {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
        // dpm.setGlobalSetting(deviceOwner, "user_switcher_enabled",status.toInt().toString())
    }

    override suspend fun getUserSwitcherStatus(): Boolean {
        throw SuperUserException(
            NO_ROOT_RIGHTS,
            UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
        )
    }


    companion object {
        private const val INSTALL_COMPLETE = "DHIZUKU_INSTALL_COMPLETE"
        private const val NO_OWNER_RIGHTS = "App doesn't have owner rights."
        private const val ANDROID_VERSION_INCORRECT =
            "Wrong android version, SDK version %s or higher required"
        private const val PRIMARY_USER_LOGOUT = "You can't logout from primary user"
        private const val NO_ROOT_RIGHTS = "App doesn't have root rights"
        private const val NOT_ENOUGH_RIGHTS = "App doesn't have enough rights"
    }

}