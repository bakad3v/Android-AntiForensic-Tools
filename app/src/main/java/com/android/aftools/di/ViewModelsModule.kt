package com.android.aftools.di

import com.android.aftools.presentation.actions.DialogActions
import com.android.aftools.presentation.actions.FileSettingsAction
import com.android.aftools.presentation.actions.LogsActions
import com.android.aftools.presentation.actions.SettingsAction
import com.android.aftools.presentation.states.ActivityState
import com.android.aftools.presentation.states.LogsDataState
import com.android.aftools.presentation.states.PasswordState
import com.android.aftools.presentation.states.RootState
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
  fun providePasswordStateFlow(): MutableSharedFlow<PasswordState> = MutableSharedFlow()

  @Provides
  fun provideDeletionSettingsActionChannel(): Channel<FileSettingsAction> = Channel()

  @Provides
  fun provideLogsStateFlow(): MutableSharedFlow<LogsDataState> = MutableSharedFlow()

  @Provides
  fun provideLogsActionChannel(): Channel<LogsActions> = Channel()

  @Provides
  fun provideActivityStateFlow(): MutableStateFlow<ActivityState> = MutableStateFlow(ActivityState.NoActionBarActivityState)

  @Provides
  fun provideDialogActionsChannel(): Channel<DialogActions> = Channel()

  @Provides
  fun provideSettingsActionsChannel(): Channel<SettingsAction> = Channel()

  @Provides
  fun provideRootState(): MutableStateFlow<RootState> = MutableStateFlow(RootState.Loading)
}
