package com.android.aftools.di

import com.sonozaki.files.repository.FilesRepository
import com.sonozaki.files.repository.FilesRepositoryImpl
import com.sonozaki.logs.repository.LogsRepository
import com.sonozaki.logs.repository.LogsRepositoryImpl
import com.sonozaki.profiles.repository.ProfilesRepository
import com.sonozaki.profiles.repository.ProfilesRepositoryImpl
import com.sonozaki.root.repository.RootRepositoryImpl
import com.sonozaki.rootcommands.domain.repository.RootScreenRepository
import com.sonozaki.settings.repositories.BruteforceRepository
import com.sonozaki.settings.repositories.BruteforceSettingsRepositoryImpl
import com.sonozaki.settings.repositories.ButtonSettingsRepository
import com.sonozaki.settings.repositories.ButtonSettingsRepositoryImpl
import com.sonozaki.settings.repositories.PermissionsRepository
import com.sonozaki.settings.repositories.PermissionsRepositoryImpl
import com.sonozaki.settings.repositories.SettingsRepository
import com.sonozaki.settings.repositories.SettingsRepositoryImpl
import com.sonozaki.settings.repositories.USBSettingsRepositoryImpl
import com.sonozaki.settings.repositories.UsbSettingsRepository
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
    abstract fun provideRootRepository(rootRepositoryImpl: RootRepositoryImpl): RootScreenRepository

    @Binds
    @Singleton
    abstract fun provideButtonRepository(buttonSettingsRepositoryImpl: ButtonSettingsRepositoryImpl): ButtonSettingsRepository
}