package com.sonozaki.settings.presentation.viewmodel

import com.sonozaki.dialogs.DialogActions
import com.sonozaki.settings.domain.usecases.permissions.GetPermissionsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@HiltViewModel
class SettingsVM @Inject constructor(
    getPermissionsUseCase: GetPermissionsUseCase,
    getSettingsUseCase: GetSettingsUseCase,
    settingsActionChannel: Channel<DialogActions>,
): AbstractSettingsVM(settingsActionChannel, getSettingsUseCase, getPermissionsUseCase) {

}