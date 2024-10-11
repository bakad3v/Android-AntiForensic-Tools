package com.android.aftools.di

import com.android.aftools.data.repositories.ProfilesRepositoryImpl
import com.android.aftools.domain.entities.ProfileDomain
import com.android.aftools.domain.repositories.ProfilesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfilesModule {
    @Binds
    @Singleton
    abstract fun provideProfilesRepository(profilesRepositoryImpl: ProfilesRepositoryImpl): ProfilesRepository

    companion object {
        @Provides
        @Singleton
        fun provideProfilesFlow(): MutableSharedFlow<List<ProfileDomain>> = MutableSharedFlow()
    }
}