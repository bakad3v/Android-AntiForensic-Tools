package com.oasisfeng.island.di

import com.oasisfeng.island.domain.entities.ProfileDomain
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProfilesModule {
    @Provides
    @Singleton
    fun provideProfilesFlow(): MutableSharedFlow<List<ProfileDomain>> = MutableSharedFlow()
}