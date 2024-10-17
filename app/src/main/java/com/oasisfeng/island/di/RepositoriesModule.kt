package com.oasisfeng.island.di

import com.oasisfeng.island.data.repositories.AppsRepositoryImpl
import com.oasisfeng.island.data.repositories.BruteforceSettingsRepositoryImpl
import com.oasisfeng.island.data.repositories.FilesRepositoryImpl
import com.oasisfeng.island.data.repositories.LogsRepositoryImpl
import com.oasisfeng.island.data.repositories.PermissionsRepositoryImpl
import com.oasisfeng.island.data.repositories.ProfilesRepositoryImpl
import com.oasisfeng.island.data.repositories.RootRepositoryImpl
import com.oasisfeng.island.data.repositories.SettingsRepositoryImpl
import com.oasisfeng.island.data.repositories.USBSettingsRepositoryImpl
import com.oasisfeng.island.domain.repositories.AppsRepository
import com.oasisfeng.island.domain.repositories.BruteforceRepository
import com.oasisfeng.island.domain.repositories.FilesRepository
import com.oasisfeng.island.domain.repositories.LogsRepository
import com.oasisfeng.island.domain.repositories.PermissionsRepository
import com.oasisfeng.island.domain.repositories.ProfilesRepository
import com.oasisfeng.island.domain.repositories.RootRepository
import com.oasisfeng.island.domain.repositories.SettingsRepository
import com.oasisfeng.island.domain.repositories.UsbSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindAppsRepository(appsRepositoryImpl: AppsRepositoryImpl): AppsRepository

    @Binds
    @Singleton
    abstract fun bindFilesRepository(filesRepositoryImpl: FilesRepositoryImpl): FilesRepository

    @Binds
    @Singleton
    abstract fun bindLogsRepository(filesRepositoryImpl: LogsRepositoryImpl): LogsRepository

    @Binds
    @Singleton
    abstract fun provideSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun providePermissionsRepository(permissionsRepositoryImpl: PermissionsRepositoryImpl): PermissionsRepository

    @Binds
    @Singleton
    abstract fun provideBruteforceRepository(bruteforceSettingsRepositoryImpl: BruteforceSettingsRepositoryImpl): BruteforceRepository

    @Binds
    @Singleton
    abstract fun provideUsbSettingsRepository(usbSettingsRepositoryImpl: USBSettingsRepositoryImpl): UsbSettingsRepository

    @Binds
    @Singleton
    abstract fun provideProfilesRepository(profilesRepositoryImpl: ProfilesRepositoryImpl): ProfilesRepository

    @Binds
    @Singleton
    abstract fun provideRootRepository(rootRepositoryImpl: RootRepositoryImpl): RootRepository
}