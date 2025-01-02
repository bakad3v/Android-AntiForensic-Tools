package com.oasisfeng.island.di

import com.oasisfeng.island.data.repositories.PasswordManagerImpl
import com.oasisfeng.island.domain.repositories.PasswordManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PasswordModule {

    @Binds
    @Singleton
    abstract fun bindPasswordManager(passwordManagerImpl: PasswordManagerImpl): PasswordManager
}