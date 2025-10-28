package com.sonozaki.superuser.dshizuku

import com.sonozaki.entities.ProfileDomain
import com.sonozaki.entities.ShizukuState
import com.sonozaki.superuser.domain.usecases.GetPermissionsUseCase
import com.sonozaki.superuser.owner.DhizukuManager
import com.sonozaki.superuser.shizuku.ShizukuManager
import com.sonozaki.superuser.superuser.SuperUser
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.utils.UIText
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.flow.first
import okio.BufferedSource
import javax.inject.Inject

class Dshizuku @Inject constructor(
    private val shizukuManager: ShizukuManager,
    private val dhizukuManager: DhizukuManager,
    private val getPermissionsUseCase: GetPermissionsUseCase
): SuperUser {


    /**
     * Function for choosing shizuku or dhizuku to run a command.
     * @return true if shizuku, false if dhizuku
     */
    private suspend fun shizukuDhizukuNormalFlow(): Boolean {
        return if (shizukuManager.shizukuStateFlow.first() == ShizukuState.INITIALIZED) {
            true
        } else if (getPermissionsUseCase().isOwner){
           false
        } else {
            true
        }
    }

    //available only for dhizuku
    override suspend fun wipe() {
        if (getPermissionsUseCase().isOwner) {
            dhizukuManager.wipe()
        } else {
            shizukuManager.wipe()
        }
    }

    override suspend fun getProfiles(): List<ProfileDomain> {
        return if (shizukuDhizukuNormalFlow()) {
            shizukuManager.getProfiles()
        } else {
            dhizukuManager.getProfiles()
        }
    }

    //Function available only for dhizuku
    override suspend fun removeProfile(id: Int) {
        return if (getPermissionsUseCase().isOwner) {
            dhizukuManager.removeProfile(id)
        } else {
            shizukuManager.removeProfile(id)
        }
    }

    //Function available only for shizuku
    override suspend fun uninstallApp(packageName: String) {
        if (getPermissionsUseCase().isShizuku) {
            shizukuManager.uninstallApp(packageName)
        } else {
            dhizukuManager.uninstallApp(packageName)
        }
    }

    //Function available only for dhizuku
    override suspend fun hideApp(packageName: String) {
         if (getPermissionsUseCase().isOwner) {
             dhizukuManager.hideApp(packageName)
        } else {
             shizukuManager.hideApp(packageName)
        }
    }

    //Function available only for dhizuku
    override suspend fun clearAppData(packageName: String) {
        if (getPermissionsUseCase().isOwner) {
            dhizukuManager.clearAppData(packageName)
        } else {
           shizukuManager.clearAppData(packageName)
        }
    }

    //Function available only for shizuku
    override suspend fun runTrim() {
        if (getPermissionsUseCase().isShizuku) {
            shizukuManager.runTrim()
        } else {
            dhizukuManager.runTrim()
        }
    }

    override suspend fun executeRootCommand(command: String): Shell.Result {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights))
    }

    override suspend fun stopLogd() {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights))
    }

    override fun getPowerButtonClicks(callback: (Boolean) -> Unit): () -> Unit {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)) }

    override suspend fun setMultiuserUI(status: Boolean) {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights))
    }

    override suspend fun getMultiuserUIStatus(): Boolean {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights))
    }

    override suspend fun setUsersLimit(limit: Int) {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights))
    }

    override suspend fun getUserLimit(): Int? {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights))
    }

    override suspend fun setSafeBootStatus(status: Boolean) {
        if (shizukuDhizukuNormalFlow()) {
            shizukuManager.setSafeBootStatus(status)
        } else {
            dhizukuManager.setSafeBootStatus(status)
        }
    }

    override suspend fun getSafeBootStatus(): Boolean {
        return if (shizukuDhizukuNormalFlow()) {
            shizukuManager.getSafeBootStatus()
        } else {
            dhizukuManager.getSafeBootStatus()
        }
    }

    //Function available only for shizuku
    override suspend fun setUserSwitcherStatus(status: Boolean) {
        if (getPermissionsUseCase().isShizuku) {
            shizukuManager.setUserSwitcherStatus(status)
        } else {
            dhizukuManager.setUserSwitcherStatus(status)
        }
    }

    //Function available only for shizuku
    override suspend fun getUserSwitcherStatus(): Boolean {
        return if (getPermissionsUseCase().isShizuku) {
            shizukuManager.getUserSwitcherStatus()
        } else {
            dhizukuManager.getUserSwitcherStatus()
        }
    }

    //Function available only for dhizuku
    override suspend fun setSwitchUserRestriction(status: Boolean) {
        if (getPermissionsUseCase().isOwner) {
            dhizukuManager.setSwitchUserRestriction(status)
        } else {
            shizukuManager.setSwitchUserRestriction(status)
        }
    }

    //Function available only for dhizuku
    override suspend fun getSwitchUserRestriction(): Boolean {
        return if (getPermissionsUseCase().isOwner) {
            dhizukuManager.getSwitchUserRestriction()
        } else {
            shizukuManager.getSwitchUserRestriction()
        }
    }

    override suspend fun reboot() {
        if (shizukuDhizukuNormalFlow()) {
            shizukuManager.reboot()
        } else {
            dhizukuManager.reboot()
        }
    }

    override suspend fun stopProfile(
        userId: Int,
        isCurrent: Boolean
    ): Boolean {
        return if (shizukuDhizukuNormalFlow()) {
            shizukuManager.stopProfile(userId, isCurrent)
        } else {
            dhizukuManager.stopProfile(userId, isCurrent)
        }
    }

    override suspend fun installTestOnlyApp(
        length: Long,
        data: BufferedSource
    ): Boolean {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights))
    }

    //Function available only for shizuku
    override suspend fun changeLogsStatus(enable: Boolean) {
        if (getPermissionsUseCase().isShizuku) {
            shizukuManager.changeLogsStatus(enable)
        } else {
            dhizukuManager.changeLogsStatus(enable)
        }
    }

    //Function available only for shizuku
    override suspend fun changeDeveloperSettingsStatus(unlock: Boolean) {
        if (getPermissionsUseCase().isShizuku) {
            shizukuManager.changeDeveloperSettingsStatus(unlock)
        } else {
            dhizukuManager.changeDeveloperSettingsStatus(unlock)
        }
    }

    //Function available only for shizuku
    override suspend fun getLogsStatus(): Boolean {
        return if (getPermissionsUseCase().isShizuku) {
            shizukuManager.getLogsStatus()
        } else {
            dhizukuManager.getLogsStatus()
        }
    }

    //Function available only for shizuku
    override suspend fun getDeveloperSettingsStatus(): Boolean {
        return if (getPermissionsUseCase().isShizuku) {
            shizukuManager.getDeveloperSettingsStatus()
        } else {
            dhizukuManager.getDeveloperSettingsStatus()
        }
    }

    override suspend fun openProfile(userId: Int) {
        if (shizukuDhizukuNormalFlow()) {
            shizukuManager.openProfile(userId)
        } else {
            dhizukuManager.openProfile(userId)
        }
    }

    override suspend fun removeNotification(packageName: String, id: Int) {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights))
    }

    companion object {
        private const val NO_ROOT_RIGHTS = "App doesn't have root rights"
    }
}