package com.sonozaki.rootcommands.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.rootcommands.R
import com.sonozaki.rootcommands.domain.usecases.GetRootCommandUseCase
import com.sonozaki.rootcommands.domain.usecases.GetRootEnabledUseCase
import com.sonozaki.rootcommands.domain.usecases.SetRootCommandUseCase
import com.sonozaki.rootcommands.domain.usecases.SetRunRootUseCase
import com.sonozaki.rootcommands.presentation.state.RootState
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootVM @Inject constructor(
    private val getRootCommandUseCase: GetRootCommandUseCase,
    private val setRootCommandUseCase: SetRootCommandUseCase,
    private val setRunRootUseCase: SetRunRootUseCase,
    getRootEnabledUseCase: GetRootEnabledUseCase,
    private val _rootState: MutableStateFlow<RootState>,
    private val dialogActionsChannel: Channel<DialogActions>
): ViewModel() {

    val rootActions = dialogActionsChannel.receiveAsFlow()

    val rootCommandEnabled = getRootEnabledUseCase().stateIn(
            scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(0, 0),
    initialValue = false
    )

    val rootState = _rootState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(0, 0),
        initialValue = RootState.Loading
    )

    init {
        viewModelScope.launch {
            val command = getRootCommandUseCase()
            if (command == null) {
                _rootState.value = RootState.NoRoot
                return@launch
            }
            _rootState.value = RootState.LoadCommand(command)
        }
    }

    fun edit() {
        _rootState.value = RootState.EditData
    }

    fun setRunRoot(status: Boolean) {
        viewModelScope.launch {
            setRunRootUseCase(status)
        }
    }

    fun saveRootCommand(command: String) {
        viewModelScope.launch {
            setRootCommandUseCase(command)
            _rootState.value = RootState.ViewData
        }
    }

    fun showChangeDeletionEnabledDialog() {
        viewModelScope.launch {
            if (rootCommandEnabled.value) {
                setRunRootUseCase(false)
                return@launch
            }
            dialogActionsChannel.send(
                DialogActions.ShowQuestionDialog(
                    title = UIText.StringResource(R.string.enable_root_command),
                    message = UIText.StringResource(R.string.enable_root_command_long),
                    ENABLE_ROOT_COMMANDS_DIALOG
                )
            )
        }
    }

    fun showNoRootRightsDialog() {
        viewModelScope.launch {
            dialogActionsChannel.send(
                DialogActions.ShowQuestionDialog(
                    title = UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights),
                    message = UIText.StringResource(R.string.no_root_rights_commands),
                    hideCancel = true,
                    cancellable = false,
                    requestKey = NO_SUPERUSER
                )
            )
        }
    }

    companion object {
        const val ENABLE_ROOT_COMMANDS_DIALOG = "enable_root_commands_dialog"
        const val NO_SUPERUSER = "no_superuser"
    }
}