package com.sonozaki.superuser.superuser

import android.content.Intent
import com.sonozaki.entities.ShizukuState
import com.sonozaki.superuser.admin.DeviceAdmin
import com.sonozaki.superuser.domain.usecases.GetPermissionsUseCase
import com.sonozaki.superuser.dshizuku.Dshizuku
import com.sonozaki.superuser.owner.DhizukuManager
import com.sonozaki.superuser.root.MagiskManager
import com.sonozaki.superuser.root.Root
import com.sonozaki.superuser.shizuku.ShizukuManager
import com.sonozaki.utils.UIText
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class for retrieving superusers and managing permissions.
 */
@Singleton
class SuperUserManager @Inject constructor(private val dshizuku: Dshizuku, private val dhizukuManager: DhizukuManager, private val root: Root, private val admin: DeviceAdmin, private val getPermissionsUseCase: GetPermissionsUseCase, private val shizukuManager: ShizukuManager, private val magiskManager: MagiskManager) {

    /**
     * Check root access with a fresh shell, prompting on root managers that support it.
     */
    suspend fun askRootRights(): Boolean = root.refreshRootRights()

    fun rootPermissionIntent(): Intent? = magiskManager.permissionIntent()

    fun magiskLaunchIntent(): Intent? = magiskManager.launchIntent()

    fun askShizukuRights() = shizukuManager.requestShizukuPermission()

    fun checkShizukuInstalled() = shizukuManager.checkShizukuInstalled()

    val shizukuStateFlow = shizukuManager.shizukuStateFlow

    /**
     * Show dialog with Dhizuku permission request.
     * @param onApprove lambda to run if permission granted
     * @param onDeny lambda to run if permission denied
     * @param onAbsent lambda to run if no Dhizuku process found
     */
    fun askDeviceOwnerRights(onApprove: () -> Unit, onDeny: () -> Unit, onAbsent: () -> Unit) = dhizukuManager.askSuperUserRights(onApprove, onDeny, onAbsent)

    /**
     * Show dialog with admin rights request.
     */
    fun askDeviceAdminRights(): Intent = admin.askSuperUserRights()

    /**
     * Remove admin rights.
     */
    suspend fun removeAdminRights() = admin.removeAdminRights()

    fun hasAdminRights(): Boolean = admin.hasAdminRights()

    /** Select a working backend before revoking an admin that will need reactivation. */
    suspend fun getDeviceAdminActivator(): SuperUser? {
        val permissions = getPermissionsUseCase()
        if (permissions.isRoot && root.refreshRootRights()) {
            return root
        }
        if (permissions.isShizuku && shizukuStateFlow.value == ShizukuState.INITIALIZED &&
            shizukuManager.runCommand("true").isSuccess) {
            return shizukuManager
        }
        return null
    }

    /**
     * Get the most privileged superuser available
     */
    suspend fun getSuperUser(): SuperUser {
        val permissions = getPermissionsUseCase()
        if (permissions.isRoot)
            return root
        if (permissions.isOwner || permissions.isShizuku)
            return dshizuku
        if (permissions.isAdmin)
            return admin
        throw SuperUserException(NO_SUPERUSER_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_superuser_rights))
    }

    companion object {
        private const val NO_SUPERUSER_RIGHTS = "You don't have superuser rights"
    }
}
