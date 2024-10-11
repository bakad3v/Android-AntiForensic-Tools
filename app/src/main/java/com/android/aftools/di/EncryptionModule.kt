package com.android.aftools.di

import com.android.aftools.data.encryption.EncryptionManager
import com.android.aftools.data.encryption.EncryptionManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EncryptionModule {


    @Binds
    @Singleton
    abstract fun provideEncryptionManager(encryptionManagerImpl: EncryptionManagerImpl): EncryptionManager
}