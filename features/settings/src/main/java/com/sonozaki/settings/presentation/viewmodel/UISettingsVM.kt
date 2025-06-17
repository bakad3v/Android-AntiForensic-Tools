package com.sonozaki.settings.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.entities.Theme
import com.sonozaki.settings.R
import com.sonozaki.settings.domain.usecases.permissions.GetPermissionsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetSettingsUseCase
import com.sonozaki.settings.domain.usecases.settings.SetScreenshotsStatusUseCase
import com.sonozaki.settings.domain.usecases.settings.SetThemeUseCase
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UISettingsVM @Inject constructor(
    private val setScreenshotsStatusUseCase: SetScreenshotsStatusUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    settingsActionChannel: Channel<DialogActions>,
    getSettingsUseCase: GetSettingsUseCase,
    getPermissionsUseCase: GetPermissionsUseCase
): AbstractSettingsVM(settingsActionChannel, getSettingsUseCase, getPermissionsUseCase) {

    fun showAllowScreenShotsDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.allow_screenshots),
            message = UIText.StringResource(R.string.allow_screenshots_long),
            ALLOW_SCREENSHOTS_DIALOG
        )
    }

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }

    fun setScreenShotsStatus(status: Boolean) {
        viewModelScope.launch {
            setScreenshotsStatusUseCase(status)
        }
    }

    companion object {
        const val ALLOW_SCREENSHOTS_DIALOG = "allow_screenshots_dialog"
    }
}