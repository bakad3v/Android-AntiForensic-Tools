package com.android.aftools.di

import com.android.aftools.data.repositories.AppsRepositoryImpl
import com.android.aftools.data.repositories.BruteforceSettingsRepositoryImpl
import com.android.aftools.data.repositories.FilesRepositoryImpl
import com.android.aftools.data.repositories.LogsRepositoryImpl
import com.android.aftools.data.repositories.PermissionsRepositoryImpl
import com.android.aftools.data.repositories.ProfilesRepositoryImpl
import com.android.aftools.data.repositories.RootRepositoryImpl
import com.android.aftools.data.repositories.SettingsRepositoryImpl
import com.android.aftools.data.repositories.USBSettingsRepositoryImpl
import com.android.aftools.domain.repositories.AppsRepository
import com.android.aftools.domain.repositories.BruteforceRepository
import com.android.aftools.domain.repositories.FilesRepository
import com.android.aftools.domain.repositories.LogsRepository
import com.android.aftools.domain.repositories.PermissionsRepository
import com.android.aftools.domain.repositories.ProfilesRepository
import com.android.aftools.domain.repositories.RootRepository
import com.android.aftools.domain.repositories.SettingsRepository
import com.android.aftools.domain.repositories.UsbSettingsRepository
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