package com.sonozaki.settings.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.bakasoft.network.NetworkError
import com.bakasoft.network.RequestResult
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.entities.Permissions
import com.sonozaki.settings.R
import com.sonozaki.settings.domain.usecases.appUpdate.DisableUpdatePopupUseCase
import com.sonozaki.settings.domain.usecases.appUpdate.DownloadUpdateUseCase
import com.sonozaki.settings.domain.usecases.appUpdate.GetUpdatePopupStatusUseCase
import com.sonozaki.settings.domain.usecases.permissions.GetPermissionsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetSettingsUseCase
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.superuser.superuser.SuperUserManager
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class SettingsVM @Inject constructor(
    getUpdatePopupStatusUseCase: GetUpdatePopupStatusUseCase,
    getPermissionsUseCase: GetPermissionsUseCase,
    getSettingsUseCase: GetSettingsUseCase,
    settingsActionChannel: Channel<DialogActions>,
    private val superUserManager: SuperUserManager,
    private val downloadUpdateUseCase: DownloadUpdateUseCase,
    private val disableUpdatePopupStatusUseCase: DisableUpdatePopupUseCase

): AbstractSettingsVM(settingsActionChannel, getSettingsUseCase, getPermissionsUseCase) {

    private fun networkErrorToText(error: NetworkError): UIText.StringResource {
        return when(error) {
            is NetworkError.EmptyResponse -> UIText.StringResource(R.string.empty_response)
            is NetworkError.ConnectionError -> UIText.StringResource(R.string.connection_error)
            is NetworkError.ServerError -> UIText.StringResource(R.string.server_error, error.code, error.description)
            is NetworkError.UnknownError -> UIText.StringResource(R.string.unknown_error, error.error)
        }
    }

    private suspend fun installTestOnlyUpdate(result: RequestResult.Data<ResponseBody>) {
        try {
            superUserManager.getSuperUser().installTestOnlyApp(result.data.contentLength(), result.data.source())
        } catch (e: SuperUserException) {
            showInfoDialogSuspend(
                UIText.StringResource(R.string.superuser_exception),
                message = e.messageForLogs
            )
        } catch (e: Exception) {
            showInfoDialogSuspend(
                UIText.StringResource(R.string.unknown_exception),
                UIText.StringResource(R.string.unknown_error, e.message ?:"")
            )
        }
    }

    fun updateApp() {
        viewModelScope.launch {
            val result = downloadUpdateUseCase()
            when(result) {
                is RequestResult.Data<ResponseBody> -> {
                    installTestOnlyUpdate(result)
                }
                is RequestResult.Error -> {
                    showInfoDialogSuspend(
                        UIText.StringResource(R.string.network_error),
                        networkErrorToText(result.error)
                    )
                }
            }
        }
    }

    fun disableUpdatePopup() {
        viewModelScope.launch {
            disableUpdatePopupStatusUseCase()
        }
    }

    val updatePopupStatusFlow = getUpdatePopupStatusUseCase().combine(getPermissionsUseCase()) { updatePopup: Boolean, permissions: Permissions ->
        updatePopup && permissions.isRoot
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
        false
    )
}