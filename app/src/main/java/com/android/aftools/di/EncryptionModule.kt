package com.android.aftools.di


import com.sonozaki.encrypteddatastore.encryption.EncryptionManager
import com.sonozaki.encrypteddatastore.encryption.EncryptionManagerImpl
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