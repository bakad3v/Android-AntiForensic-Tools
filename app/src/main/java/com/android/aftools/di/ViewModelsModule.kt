package com.android.aftools.di

import com.bakasoft.appupdatecenter.presentation.actions.AppUpdaterActions
import com.bakasoft.appupdatecenter.presentation.state.SelectedOption
import com.bakasoft.setupwizard.presentation.actions.ConfirmationFragmentsActions
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.files.presentation.actions.FileSettingsAction
import com.sonozaki.logs.presentation.actions.LogsActions
import com.sonozaki.logs.presentation.state.LogsDataState
import com.sonozaki.lockscreen.presentation.state.EnterPasswordState
import com.sonozaki.passwordsetup.presentation.actions.PasswordActions
import com.sonozaki.rootcommands.presentation.state.RootState
import com.sonozaki.utils.UIText
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

@InstallIn(ViewModelComponent::class)
@Module
class ViewModelsModule {

  @Provides
  fun providePasswordStateFlow(): MutableSharedFlow<EnterPasswordState> = MutableSharedFlow()

  @Provides
  fun provideSelectedOptionFlow(): MutableStateFlow<SelectedOption> = MutableStateFlow(
    SelectedOption.NONE)

  @Provides
  fun provideErrorsChannel(): Channel<UIText.StringResource> = Channel()

  @Provides
  fun provideDeletionSettingsActionChannel(): Channel<FileSettingsAction> = Channel()

  @Provides
  fun provideLogsStateFlow(): MutableSharedFlow<LogsDataState> = MutableSharedFlow()

  @Provides
  fun provideFABSFlow(): MutableStateFlow<Boolean> = MutableStateFlow(false)

  @Provides
  fun provideLogsActionChannel(): Channel<LogsActions> = Channel()

  @Provides
  fun provideActivityStateFlow(): MutableStateFlow<ActivityState> = MutableStateFlow(
      ActivityState.NoActionBarActivityState)

  @Provides
  fun provideDialogActionsChannel(): Channel<DialogActions> = Channel()

  @Provides
  fun providePasswordCreatedChannel(): Channel<PasswordActions> = Channel()

  @Provides
  fun provideRootState(): MutableStateFlow<RootState> = MutableStateFlow(RootState.Loading)

  @Provides
  fun provideUpdaterActionsChannel(): Channel<AppUpdaterActions> = Channel()

  @Provides
  fun provideWizardActionsChannel(): Channel<ConfirmationFragmentsActions> = Channel()
}
