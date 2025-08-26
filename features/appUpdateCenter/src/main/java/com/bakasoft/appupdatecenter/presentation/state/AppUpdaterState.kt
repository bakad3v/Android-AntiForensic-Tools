package com.bakasoft.appupdatecenter.presentation.state

import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.utils.UIText

sealed class AppUpdaterState {
    data object Loading: AppUpdaterState()
    data class Data(val appLatestVersion: AppLatestVersion,
                    val activeUsualVersion: Boolean,
                    val activeTestOnlyVersion: Boolean,
                    val showUpdatePopup: Boolean,
                    val selectedOption: SelectedOption = SelectedOption.NONE
    ): AppUpdaterState()
    data class Error(val error: UIText.StringResource, val showUpdatePopup: Boolean): AppUpdaterState()
}

enum class SelectedOption {
    NONE, INSTALL_USUAL, INSTALL_TESTONLY
}