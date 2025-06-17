package com.sonozaki.settings.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.settings.R
import com.sonozaki.settings.domain.usecases.permissions.GetPermissionsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetSettingsUseCase
import com.sonozaki.settings.domain.usecases.settings.SetCleaItselfUseCase
import com.sonozaki.settings.domain.usecases.settings.SetClearDataUseCase
import com.sonozaki.settings.domain.usecases.settings.SetHideUseCase
import com.sonozaki.settings.domain.usecases.settings.SetRemoveItselfUseCase
import com.sonozaki.settings.domain.usecases.settings.SetTrimUseCase
import com.sonozaki.settings.domain.usecases.settings.SetWipeUseCase
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataDestructionSettingsVM @Inject constructor(
    private val setRemoveItselfUseCase: SetRemoveItselfUseCase,
    private val setTrimUseCase: SetTrimUseCase,
    private val setWipeUseCase: SetWipeUseCase,
    private val setHideUseCase: SetHideUseCase,
    private val setClearDataUseCase: SetClearDataUseCase,
    private val setClearItselfUseCase: SetCleaItselfUseCase,
    settingsActionChannel: Channel<DialogActions>,
    getSettingsUseCase: GetSettingsUseCase,
    getPermissionsUseCase: GetPermissionsUseCase
): AbstractSettingsVM(settingsActionChannel, getSettingsUseCase, getPermissionsUseCase) {

    fun setHide(status: Boolean) {
        viewModelScope.launch {
            setHideUseCase(status)
        }
    }

    fun showHideDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.hide_app),
            message = UIText.StringResource(R.string.hide_long),
            HIDE_DIALOG
        )
    }

    fun setClear(status: Boolean) {
        viewModelScope.launch {
            setClearItselfUseCase(status)
        }
    }

    fun showClearDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.clear_itself),
            message = UIText.StringResource(R.string.clear_itself_long),
            CLEAR_DIALOG
        )
    }

    fun setClearData(status: Boolean) {
        viewModelScope.launch {
            setClearDataUseCase(status)
        }
    }

    fun showClearDataDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.clear_data),
            message = UIText.StringResource(R.string.clear_data_long),
            CLEAR_DATA_DIALOG
        )
    }

    fun setRunTRIM(status: Boolean) {
        viewModelScope.launch {
            setTrimUseCase(status)
        }
    }

    fun setWipe(status: Boolean) {
        viewModelScope.launch {
            setWipeUseCase(status)
        }
    }

    fun setRemoveItself(status: Boolean) {
        viewModelScope.launch {
            setRemoveItselfUseCase(status)
        }
    }

    fun showTRIMDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.run_trim),
            message = UIText.StringResource(R.string.trim_long),
            TRIM_DIALOG
        )
    }

    fun showWipeDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.wipe_data),
            message = UIText.StringResource(R.string.wipe_long),
            WIPE_DIALOG
        )
    }

    fun showSelfDestructionDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.remove_itself),
            message = UIText.StringResource(R.string.self_destruct_long),
            SELF_DESTRUCTION_DIALOG
        )
    }

    fun destroyDataDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.destroy_data),
            message = UIText.StringResource(R.string.destroy_data_long),
            DESTROY_DATA_DIALOG
        )
    }

    companion object {
        const val TRIM_DIALOG = "trim_dialog"
        const val WIPE_DIALOG = "wipe_dialog"
        const val SELF_DESTRUCTION_DIALOG = "selfdestruct_dialog"
        const val HIDE_DIALOG = "hide_dialog"
        const val CLEAR_DIALOG = "clear_dialog"
        const val CLEAR_DATA_DIALOG = "clear_data_dialog"
        const val DESTROY_DATA_DIALOG = "destroy_data_dialog"
    }
}