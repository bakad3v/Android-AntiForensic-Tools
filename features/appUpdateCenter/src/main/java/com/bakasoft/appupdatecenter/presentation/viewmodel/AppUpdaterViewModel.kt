package com.bakasoft.appupdatecenter.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakasoft.appupdatecenter.domain.usecases.CheckUpdatesUseCase
import com.bakasoft.appupdatecenter.domain.usecases.GetAppUpdateDataFlowUseCase
import com.bakasoft.appupdatecenter.domain.usecases.GetPermissionsUseCase
import com.bakasoft.appupdatecenter.domain.usecases.GetPopupStatusUseCase
import com.bakasoft.appupdatecenter.domain.usecases.InstallUpdateUseCase
import com.bakasoft.appupdatecenter.domain.usecases.SetAppInstallerDataUseCase
import com.bakasoft.appupdatecenter.domain.usecases.SetUpdatePopupStatusUseCase
import com.bakasoft.appupdatecenter.presentation.actions.AppUpdaterActions
import com.bakasoft.appupdatecenter.presentation.state.AppUpdaterState
import com.bakasoft.appupdatecenter.presentation.state.SelectedOption
import com.bakasoft.network.RequestResult
import com.sonozaki.utils.TopLevelFunctions.networkErrorToText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppUpdaterViewModel @Inject constructor(
    private val checkUpdatesUseCase: CheckUpdatesUseCase,
    getPopupStatusUseCase: GetPopupStatusUseCase,
    getAppUpdateDataFlowUseCase: GetAppUpdateDataFlowUseCase,
    private val selectedOptionFlow: MutableStateFlow<SelectedOption>,
    private val installUpdateUseCase: InstallUpdateUseCase,
    private val setUpdatePopupStatusUseCase: SetUpdatePopupStatusUseCase,
    private val setAppInstallerDataUseCase: SetAppInstallerDataUseCase,
    private val actionsChannel: Channel<AppUpdaterActions>,
    getPermissionsUseCase: GetPermissionsUseCase
): ViewModel() {

    init {
        checkUpdates()
    }

    val actionsFlow = actionsChannel.receiveAsFlow()

    val appUpdateCenterState = combine(getAppUpdateDataFlowUseCase(),
        getPopupStatusUseCase(), getPermissionsUseCase(), selectedOptionFlow) { appUpdate, popupStatus, permissions, selectedOption ->
        when(appUpdate) {
            is RequestResult.Error -> AppUpdaterState.Error(networkErrorToText(appUpdate.error), popupStatus)
            is RequestResult.Data -> AppUpdaterState.Data(
                appUpdate.data, appUpdate.data.newVersion,
                (permissions.isRoot || permissions.isShizuku) && (appUpdate.data.newVersion || !appUpdate.data.isTestOnly),
                popupStatus, selectedOption)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
        AppUpdaterState.Loading
    )

    fun installUpdate() {
        viewModelScope.launch {
            installUpdateUseCase()
        }
    }

    fun setInstallerData(path: String, isTestOnly: Boolean) {
        viewModelScope.launch {
            setAppInstallerDataUseCase(path, isTestOnly)
            actionsChannel.send(AppUpdaterActions.START_UPDATE)
        }
    }

    fun setUpdatePopupStatus(status: Boolean) {
        viewModelScope.launch {
            setUpdatePopupStatusUseCase(status)
        }
    }

    fun setSelectedOption(selectedOption: SelectedOption) {
        viewModelScope.launch {
            selectedOptionFlow.emit(selectedOption)
        }
    }

    fun checkUpdates() {
        viewModelScope.launch {
            checkUpdatesUseCase()
        }
    }


}