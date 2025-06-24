package com.sonozaki.settings.presentation.viewmodel

import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.settings.R
import com.sonozaki.settings.domain.usecases.permissions.GetPermissionsUseCase
import com.sonozaki.settings.domain.usecases.permissions.SetOwnerActiveUseCase
import com.sonozaki.settings.domain.usecases.permissions.SetRootActiveUseCase
import com.sonozaki.settings.domain.usecases.settings.GetSettingsUseCase
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.superuser.superuser.SuperUserManager
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionSettingsVM @Inject constructor(
    private val superUserManager: SuperUserManager,
    private val setRootActiveUseCase: SetRootActiveUseCase,
    private val setOwnerActiveUseCase: SetOwnerActiveUseCase,
    settingsActionChannel: Channel<DialogActions>,
    getSettingsUseCase: GetSettingsUseCase,
    getPermissionsUseCase: GetPermissionsUseCase
): AbstractSettingsVM(settingsActionChannel, getSettingsUseCase, getPermissionsUseCase) {

    fun showAccessibilityServiceDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.accessibility_service_title),
            message = UIText.StringResource(R.string.accessibility_service_long),
            MOVE_TO_ACCESSIBILITY_SERVICE
        )
    }

    fun adminRightsIntent(): Intent {
        return superUserManager.askDeviceAdminRights()
    }

    fun showRootWarningDialog() {
        showQuestionDialog(title = UIText.StringResource(R.string.grant_root_rights),
            message = UIText.StringResource(R.string.warning_root_rights),
            ROOT_WARNING_DIALOG
        )
    }

    fun disableAdmin() {
        viewModelScope.launch {
            try {
                superUserManager.removeAdminRights()
            } catch (e: SuperUserException) {
                showInfoDialog(
                    title = UIText.StringResource(R.string.disabling_admin_failed),
                    message = e.messageForLogs
                )
            }
        }
    }

    private fun onDhizukuRightsApprove() {
        viewModelScope.launch {
            setOwnerActiveUseCase(true)
        }
    }

    private fun onDhizukuRightsDeny() {
        viewModelScope.launch {
            setOwnerActiveUseCase(false)
        }
    }

    private fun onDhizukuAbsent() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.install_dhizuku),
            message = UIText.StringResource(R.string.install_dhizuku_long),
            requestKey = INSTALL_DIZUKU_DIALOG
        )
    }

    fun askDhizuku() {
        superUserManager.askDeviceOwnerRights(
            ::onDhizukuRightsApprove,
            ::onDhizukuRightsDeny,
            ::onDhizukuAbsent
        )
    }

    fun showRootDisableDialog() {
        viewModelScope.launch {
            showInfoDialogSuspend(
                message = UIText.StringResource(R.string.disable_root_long),
                title = UIText.StringResource(R.string.disable_root)
            )
            setRootActiveUseCase(false)
        }
    }

    fun askRoot() {
        viewModelScope.launch {
            val rootResult = superUserManager.askRootRights()
            setRootActiveUseCase(rootResult)
        }
    }

    companion object {
        const val MOVE_TO_ACCESSIBILITY_SERVICE = "move_to_accessibility_service"
        const val ROOT_WARNING_DIALOG = "root_warning_dialog"
        const val INSTALL_DIZUKU_DIALOG = "install_dizuku"
    }
}