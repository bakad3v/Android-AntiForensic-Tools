package net.typeblog.shelter.di

import net.typeblog.shelter.data.encryption.EncryptionManager
import net.typeblog.shelter.data.encryption.EncryptionManagerImpl
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