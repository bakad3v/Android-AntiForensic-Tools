package net.typeblog.shelter.di

import net.typeblog.shelter.data.repositories.PasswordManagerImpl
import net.typeblog.shelter.domain.repositories.PasswordManager
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