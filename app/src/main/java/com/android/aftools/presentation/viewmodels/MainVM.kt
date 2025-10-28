package com.android.aftools.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.aftools.R
import com.android.aftools.domain.entities.UpdateCheckResult
import com.android.aftools.domain.usecases.CheckUpdatesUseCase
import com.bakasoft.appupdatecenter.domain.AppUpdateRouter
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.settings.domain.usecases.settings.GetSettingsUseCase
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val _activityState: MutableStateFlow<ActivityState>,
    private val checkUpdatesUseCase: CheckUpdatesUseCase,
    getSettingsUseCase: GetSettingsUseCase,
    private val _dialogActions: Channel<DialogActions>,
    private val appUpdateRouter: AppUpdateRouter
) : ViewModel() {

    val dialogActions = _dialogActions.receiveAsFlow()

    init {
        killNotifications()
        checkUpdate()
    }

    private fun killNotifications() {
        try {
            appUpdateRouter.killNotifications()
        } catch (e: Exception) {

        }
    }



    private fun checkUpdate() {
        viewModelScope.launch {
            val result = checkUpdatesUseCase()
            showUpdatePopup(result)
        }
    }

    suspend fun showUpdatePopup(result: UpdateCheckResult) {
        val (title, text) = when(result) {
            UpdateCheckResult.CHECK_ERROR ->
                UIText.StringResource(R.string.update_check_failed) to UIText.StringResource(R.string.update_check_failed_long)
            UpdateCheckResult.CHECK_UPDATE ->
                UIText.StringResource(R.string.updates_found) to UIText.StringResource(R.string.updates_found_long)
            UpdateCheckResult.CHECK_NOTHING -> return
        }
        activityState.first { it is ActivityState.NormalActivityState }
        viewModelScope.launch {
            _dialogActions.send(
                DialogActions.ShowQuestionDialog(
                    title, text, UPDATE_POPUP_REQUEST
                )
            )
        }
    }

    val activityState: StateFlow<ActivityState> get() = _activityState.asStateFlow()

    val uiSettings = getSettingsUseCase().map { it.uiSettings }

    fun setActivityState(state: ActivityState) {
        viewModelScope.launch {
            _activityState.emit(state)
        }
    }

    companion object {
        const val UPDATE_POPUP_REQUEST = "update_popup_request"
    }
}
