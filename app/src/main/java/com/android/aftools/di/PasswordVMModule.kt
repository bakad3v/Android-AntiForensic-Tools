package com.android.aftools.di

import com.sonozaki.passwordsetup.presentation.states.SetupPasswordState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow

@Module
@InstallIn(ViewModelComponent::class)
class PasswordVMModule {

    @Provides
    @ViewModelScoped
    fun providePasswordStateFlow(): MutableSharedFlow<SetupPasswordState> = MutableSharedFlow()
}