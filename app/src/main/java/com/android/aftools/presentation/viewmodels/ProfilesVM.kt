package com.android.aftools.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.aftools.R
import com.android.aftools.domain.usecases.profiles.GetProfilesUseCase
import com.android.aftools.domain.usecases.profiles.RefreshProfilesUseCase
import com.android.aftools.domain.usecases.profiles.SetProfileDeletionStatusUseCase
import com.android.aftools.domain.usecases.settings.GetSettingsUseCase
import com.android.aftools.domain.usecases.settings.SetDeleteProfilesUseCase
import com.android.aftools.presentation.dialogs.DialogActions
import com.android.aftools.presentation.states.ProfilesDataState
import com.android.aftools.presentation.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfilesVM @Inject constructor(
    getProfilesUseCase: GetProfilesUseCase,
    private val setProfilesDeletionStatusUseCase: SetProfileDeletionStatusUseCase,
    private val setDeleteProfilesUseCase: SetDeleteProfilesUseCase,
    private val refreshProfilesUseCase: RefreshProfilesUseCase,
    private val dialogActionsChannel: Channel<DialogActions>,
    getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    val profileActions = dialogActionsChannel.receiveAsFlow()

    val profiles = getProfilesUseCase().map {
        if (it == null) {
            showNoSuperuserRightsDialog()
            ProfilesDataState.SuperUserAbsent
        } else
        ProfilesDataState.ViewData(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfilesDataState.Loading
    )

    val profileDeletionEnabled = getSettingsUseCase().map { it.deleteProfiles }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun setProfileDeletionStatus(id: Int, status: Boolean) {
        viewModelScope.launch {
            setProfilesDeletionStatusUseCase(id, status)
        }
    }

    fun changeDeletionEnabled() {
        viewModelScope.launch {
            setDeleteProfilesUseCase(!profileDeletionEnabled.value)
        }
    }

    fun refreshProfilesData() {
        viewModelScope.launch {
            refreshProfilesUseCase()
        }
    }

    fun showFAQ() {
        viewModelScope.launch {
            dialogActionsChannel.send(
                DialogActions.ShowInfoDialog(
                    title = UIText.StringResource(R.string.profiles),
                    message = UIText.StringResource(R.string.profiles_faq)
                )
            )
        }
    }

    private fun showNoSuperuserRightsDialog() {
        viewModelScope.launch {
            dialogActionsChannel.send(
                DialogActions.ShowQuestionDialog(
                    title = UIText.StringResource(R.string.no_superuser_rights),
                    message = UIText.StringResource(R.string.no_superuser_rights_profiles),
                    hideCancel = true,
                    cancellable = false,
                    requestKey = NO_SUPERUSER
                )
            )
        }
    }

    fun showNoUserSettingsDialog() {
        viewModelScope.launch {
            dialogActionsChannel.send(
                DialogActions.ShowInfoDialog(
                    title = UIText.StringResource(R.string.user_settings_disabled),
                    message = UIText.StringResource(R.string.user_settings_disabled_long)
                )
            )
        }
    }

    fun showChangeDeletionEnabledDialog() {
        viewModelScope.launch {
            if (profileDeletionEnabled.value) {
                changeDeletionEnabled()
                return@launch
            }
            dialogActionsChannel.send(
                DialogActions.ShowQuestionDialog(
                    title = UIText.StringResource(R.string.enable_profile_deletion),
                    message = UIText.StringResource(R.string.enable_profile_deletion_long),
                    requestKey = CHANGE_PROFILES_DELETION_ENABLED
                )
            )
        }
    }

    companion object {
        const val CHANGE_PROFILES_DELETION_ENABLED = "change_profiles_deletion_enabled"
        const val NO_SUPERUSER = "no_superuser"
    }
}