package com.sonozaki.superuser.superuser

import android.content.Intent
import android.util.Log
import com.sonozaki.superuser.R
import com.sonozaki.superuser.admin.DeviceAdmin
import com.sonozaki.superuser.domain.usecases.GetPermissionsUseCase
import com.sonozaki.superuser.owner.Owner
import com.sonozaki.superuser.root.Root
import com.sonozaki.utils.UIText
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class for retrieving superusers and managing permissions.
 */
@Singleton
class SuperUserManager @Inject constructor(private val owner: Owner, private val root: Root, private val admin: DeviceAdmin, private val getPermissionsUseCase: GetPermissionsUseCase) {

    /**
     * Show dialog with root rights request.
     */
    fun askRootRights(): Boolean = root.askSuperUserRights()

    /**
     * Show dialog with Dhizuku permission request.
     * @param onApprove lambda to run if permission granted
     * @param onDeny lambda to run if permission denied
     * @param onAbsent lambda to run if no Dhizuku process found
     */
    fun askDeviceOwnerRights(onApprove: () -> Unit, onDeny: () -> Unit, onAbsent: () -> Unit) = owner.askSuperUserRights(onApprove, onDeny, onAbsent)

    /**
     * Show dialog with admin rights request.
     */
    fun askDeviceAdminRights(): Intent = admin.askSuperUserRights()

    /**
     * Remove admin rights.
     */
    suspend fun removeAdminRights() = admin.removeAdminRights()

    /**
     * Get the most privileged superuser available
     */
    suspend fun getSuperUser(): SuperUser {
        Log.w("newData","permissions")
        val permissions = getPermissionsUseCase()
        Log.w("newData","permissionsEnd")
        if (permissions.isRoot)
            return root
        if (permissions.isOwner)
            return owner
        if (permissions.isAdmin)
            return admin
        throw SuperUserException(NO_SUPERUSER_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_superuser_rights))
    }

    companion object {
        private const val NO_SUPERUSER_RIGHTS = "You don't have superuser rights"
    }
}