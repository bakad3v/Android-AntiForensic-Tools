package com.android.aftools.di

import com.sonozaki.entities.ProfileDomain
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProfilesModule {
    @Provides
    @Singleton
    fun provideProfilesFlow(): MutableSharedFlow<List<ProfileDomain>> = MutableSharedFlow()
}