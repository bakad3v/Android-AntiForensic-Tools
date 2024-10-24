package com.android.aftools.superuser.superuser

import android.content.Intent
import com.android.aftools.R
import com.android.aftools.domain.usecases.permissions.GetPermissionsUseCase
import com.android.aftools.presentation.utils.UIText
import com.android.aftools.superuser.admin.DeviceAdmin
import com.android.aftools.superuser.owner.Owner
import com.android.aftools.superuser.root.Root
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Class for retrieving superusers and managing permissions.
 */
class SuperUserManager @Inject constructor(private val owner: Owner, private val root: Root, private val admin: DeviceAdmin, private val getPermissionsUseCase: GetPermissionsUseCase){

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
        val permissions = getPermissionsUseCase().first()
        if (permissions.isRoot)
            return root
        if (permissions.isOwner)
            return owner
        if (permissions.isAdmin)
            return admin
        throw SuperUserException(NO_SUPERUSER_RIGHTS,UIText.StringResource(R.string.no_superuser_rights))
    }

    companion object {
        private const val NO_SUPERUSER_RIGHTS = "You don't have superuser rights"
    }
}