package com.sonozaki.superuser.admin

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import com.sonozaki.entities.ProfileDomain
import com.sonozaki.superuser.R
import com.sonozaki.superuser.domain.usecases.SetAdminInactiveUseCase
import com.sonozaki.superuser.superuser.SuperUser
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.utils.UIText
import com.topjohnwu.superuser.Shell
import okio.BufferedSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceAdmin @Inject constructor(
    private val dpm: DevicePolicyManager,
    private val setAdminInactiveUseCase: SetAdminInactiveUseCase,
    private val deviceAdminReceiver: ComponentName
) :
    SuperUser {

    fun askSuperUserRights(): Intent {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(
            DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminReceiver
        )
        return intent
    }

    suspend fun removeAdminRights() {
        try {
            dpm.removeActiveAdmin(deviceAdminReceiver)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun checkAdminRights(): Boolean = dpm.isAdminActive(deviceAdminReceiver)

    private suspend fun handleException(e: Exception) {
        if (!checkAdminRights()) {
            setAdminInactiveUseCase()
            throw SuperUserException(
                NO_ADMIN_RIGHTS,
                UIText.StringResource(R.string.no_admin_rights)
            )
        }
        throw SuperUserException(
            e.stackTraceToString(),
            UIText.StringResource(R.string.unknow_admin_error, e.stackTraceToString())
        )
    }


    override suspend fun wipe() {
        var flags = 0
        if (VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            flags = flags.or(DevicePolicyManager.WIPE_SILENTLY)
        try {
            dpm.wipeData(flags)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun getProfiles(): List<ProfileDomain> {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun removeProfile(id: Int) {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun uninstallApp(packageName: String) {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun hideApp(packageName: String) {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun clearAppData(packageName: String) {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun runTrim() {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun executeRootCommand(command: String): Shell.Result {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun stopLogd() {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override fun getPowerButtonClicks(callback: (Boolean) -> Unit): () -> Unit {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun setMultiuserUI(status: Boolean) {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun setUsersLimit(limit: Int) {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun getUserLimit(): Int? {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun setSafeBootStatus(status: Boolean) {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun getSafeBootStatus(): Boolean {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun getMultiuserUIStatus(): Boolean {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun setUserSwitcherStatus(status: Boolean) {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun getUserSwitcherStatus(): Boolean {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun setSwitchUserRestriction(status: Boolean) {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun getSwitchUserRestriction(): Boolean {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun reboot() {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun stopProfile(userId: Int, isCurrent: Boolean): Boolean {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun installTestOnlyApp(length: Long, data: BufferedSource): Boolean {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun changeLogsStatus(enable: Boolean) {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun changeDeveloperSettingsStatus(unlock: Boolean) {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun getLogsStatus(): Boolean {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun getDeveloperSettingsStatus(): Boolean {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    override suspend fun openProfile(userId: Int) {
        throw SuperUserException(
            ADMIN_ERROR_TEXT,
            UIText.StringResource(R.string.device_admin_error)
        )
    }

    companion object {
        private const val ADMIN_ERROR_TEXT =
            "Device admin rights are not enough to perform operations."
        private const val NO_ADMIN_RIGHTS = "App doesn't have admin rights."
    }
}