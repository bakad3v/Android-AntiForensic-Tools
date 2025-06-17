package com.sonozaki.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.Settings
import com.sonozaki.settings.domain.usecases.permissions.GetPermissionsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetSettingsUseCase
import com.sonozaki.utils.UIText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class AbstractSettingsVM(
    private val settingsActionChannel: Channel<DialogActions>,
    getSettingsUseCase: GetSettingsUseCase,
    getPermissionsUseCase: GetPermissionsUseCase): ViewModel() {

    val settingsActionsFlow = settingsActionChannel.receiveAsFlow()

    val permissionsState = getPermissionsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
        Permissions()
    )

    val settingsState = getSettingsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
        Settings()
    )

    protected suspend fun showInfoDialogSuspend(
        title: UIText.StringResource,
        message: UIText.StringResource,
    ) {
        settingsActionChannel.send(
            DialogActions.ShowInfoDialog(
                title, message
            )
        )
    }

    protected fun showQuestionDialog(
        title: UIText.StringResource,
        message: UIText.StringResource,
        requestKey: String
    ) {
        viewModelScope.launch {
            showQuestionDialogSuspend(
                title, message, requestKey
            )
        }
    }

    protected suspend fun showInputDigitDialogSuspend(title: UIText.StringResource,
                                                    message: UIText.StringResource,
                                                    hint: String,
                                                    range: IntRange,
                                                    requestKey: String)  {
        settingsActionChannel.send(
            DialogActions.ShowInputDigitDialog(
                title = title,
                message = message,
                hint = hint,
                range = range,
                requestKey = requestKey
            )
        )
    }

    protected fun showInputDigitDialog(title: UIText.StringResource,
                                     message: UIText.StringResource,
                                     hint: String,
                                     range: IntRange,
                                     requestKey: String) {
        viewModelScope.launch {
            showInputDigitDialogSuspend(title,message,hint,range,requestKey)
        }
    }

    protected fun showInfoDialog(
        title: UIText.StringResource,
        message: UIText.StringResource
    ) {
        viewModelScope.launch {
            showInfoDialogSuspend(
                title, message
            )
        }
    }

    protected suspend fun showQuestionDialogSuspend(
        title: UIText.StringResource,
        message: UIText.StringResource,
        requestKey: String
    ) {
        settingsActionChannel.send(
            DialogActions.ShowQuestionDialog(
                title, message, requestKey
            )
        )
    }
}