package net.typeblog.shelter.di

import net.typeblog.shelter.presentation.actions.DialogActions
import net.typeblog.shelter.presentation.actions.FileSettingsAction
import net.typeblog.shelter.presentation.actions.LogsActions
import net.typeblog.shelter.presentation.states.ActivityState
import net.typeblog.shelter.presentation.states.LogsDataState
import net.typeblog.shelter.presentation.states.PasswordState
import net.typeblog.shelter.presentation.states.RootState
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
  fun provideActivityStateFlow(): MutableStateFlow<ActivityState> = MutableStateFlow(ActivityState.PasswordActivityState)

  @Provides
  fun provideSettingsActionsChannel(): Channel<DialogActions> = Channel()

  @Provides
  fun provideRootState(): MutableStateFlow<RootState> = MutableStateFlow(RootState.Loading)
}
