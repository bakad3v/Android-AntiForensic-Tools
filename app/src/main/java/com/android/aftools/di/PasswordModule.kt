package com.android.aftools.di

import com.android.aftools.data.repositories.PasswordManagerImpl
import com.android.aftools.domain.repositories.PasswordManager
import com.nulabinc.zxcvbn.Zxcvbn
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PasswordModule {

    @Binds
    @Singleton
    abstract fun bindPasswordManager(passwordManagerImpl: PasswordManagerImpl): PasswordManager

    companion object {
        @Provides
        @Singleton
        fun provideZxcvbn(): Zxcvbn = Zxcvbn()
    }
}