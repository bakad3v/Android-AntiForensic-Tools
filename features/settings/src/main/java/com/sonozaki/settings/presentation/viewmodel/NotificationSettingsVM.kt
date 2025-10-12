package com.sonozaki.settings.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.entities.NotificationSettings
import com.sonozaki.settings.R
import com.sonozaki.settings.domain.usecases.notifications.GetNotificationSettingsUseCase
import com.sonozaki.settings.domain.usecases.permissions.GetPermissionsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetSettingsUseCase
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsVM @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    getPermissionsUseCase: GetPermissionsUseCase,
    private val settingsActionChannel: Channel<DialogActions>,
    getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
): AbstractSettingsVM(settingsActionChannel, getSettingsUseCase, getPermissionsUseCase) {

    fun showNotificationSettingsDialog() {
        viewModelScope.launch {
            settingsActionChannel.send(
                DialogActions.ShowQuestionDialog(
                    title = UIText.StringResource(R.string.enable_notification_hiding),
                    message = UIText.StringResource(R.string.enable_notification_hiding_long),
                    requestKey = NOTIFICATION_SETTINGS_DIALOG
                )
            )
        }
    }

    val notificationSettingsState = getNotificationSettingsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
        NotificationSettings.DISABLED
    )

    companion object {
        const val NOTIFICATION_SETTINGS_DIALOG = "NOTIFICATION_SETTINGS_DIALOG"
    }
}