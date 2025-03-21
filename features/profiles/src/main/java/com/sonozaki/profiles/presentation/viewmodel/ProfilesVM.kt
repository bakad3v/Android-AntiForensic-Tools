package com.sonozaki.profiles.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.profiles.R
import com.sonozaki.profiles.domain.usecases.GetProfilesUseCase
import com.sonozaki.profiles.domain.usecases.GetDeleteProfilesUseCase
import com.sonozaki.profiles.domain.usecases.RefreshProfilesUseCase
import com.sonozaki.profiles.domain.usecases.SetDeleteProfilesUseCase
import com.sonozaki.profiles.domain.usecases.SetProfileDeletionStatusUseCase
import com.sonozaki.profiles.presentation.state.ProfilesDataState
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
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
    getDeleteProfilesUseCase: GetDeleteProfilesUseCase
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
        started = SharingStarted.WhileSubscribed(0, 0),
        initialValue = ProfilesDataState.Loading
    ).onSubscription {
        refreshProfilesUseCase()
    }

    val profileDeletionEnabled = getDeleteProfilesUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(0, 0),
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
                    title = UIText.StringResource(com.sonozaki.resources.R.string.no_superuser_rights),
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