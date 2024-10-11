package com.android.aftools.di

import com.android.aftools.data.repositories.AppsRepositoryImpl
import com.android.aftools.domain.repositories.AppsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class AppsModule {
    @Binds
    @Singleton
    abstract fun bindAppsRepository(appsRepositoryImpl: AppsRepositoryImpl): AppsRepository
}