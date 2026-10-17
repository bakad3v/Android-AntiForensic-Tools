package com.bakasoft.setupwizard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakasoft.setupwizard.R
import com.bakasoft.setupwizard.domain.entities.SetupWizardState
import com.bakasoft.setupwizard.domain.repository.SetupWizardRepository
import com.bakasoft.setupwizard.domain.usecases.GetWizardStateUseCase
import com.bakasoft.setupwizard.domain.usecases.LoadDataUseCase
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupWizardVM @Inject constructor(
    getWizardStateUseCase: GetWizardStateUseCase,
    private val loadDataUseCase: LoadDataUseCase,
    private val setupWizardRepository: SetupWizardRepository,
    private val dialogActionsChannel: Channel<DialogActions>
): ViewModel() {

    private var adminBruteforceWarningShown = false

    val dialogActionsFlow = dialogActionsChannel.receiveAsFlow()

    val wizardSate = getWizardStateUseCase()
        .onEach(::showAdminBruteforceWarningIfNeeded)
        .stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
        SetupWizardState.Loading
    ).onSubscription {
        loadDataUseCase()
    }

    private suspend fun showAdminBruteforceWarningIfNeeded(state: SetupWizardState.Data) {
        if (!state.showAdminBruteforceTestOnlyWarning || adminBruteforceWarningShown) return

        adminBruteforceWarningShown = true
        dialogActionsChannel.send(
            DialogActions.ShowQuestionDialog(
                title = UIText.StringResource(R.string.admin_bruteforce_testonly_warning_title),
                message = UIText.StringResource(R.string.admin_bruteforce_testonly_warning_message),
                requestKey = ADMIN_BRUTEFORCE_WARNING_REQUEST,
                showDoNotShowAgain = true
            )
        )
    }

    fun setAdminBruteforceWarningDisabled(disabled: Boolean) {
        viewModelScope.launch {
            setupWizardRepository.setAdminBruteforceWarningDisabled(disabled)
        }
    }

    companion object {
        const val ADMIN_BRUTEFORCE_WARNING_REQUEST = "adminBruteforceWarningRequest"
    }
}
