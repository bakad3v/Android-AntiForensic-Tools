package net.typeblog.shelter.di

import net.typeblog.shelter.data.repositories.AppsRepositoryImpl
import net.typeblog.shelter.data.repositories.BruteforceSettingsRepositoryImpl
import net.typeblog.shelter.data.repositories.FilesRepositoryImpl
import net.typeblog.shelter.data.repositories.LogsRepositoryImpl
import net.typeblog.shelter.data.repositories.PermissionsRepositoryImpl
import net.typeblog.shelter.data.repositories.ProfilesRepositoryImpl
import net.typeblog.shelter.data.repositories.RootRepositoryImpl
import net.typeblog.shelter.data.repositories.SettingsRepositoryImpl
import net.typeblog.shelter.data.repositories.USBSettingsRepositoryImpl
import net.typeblog.shelter.domain.repositories.AppsRepository
import net.typeblog.shelter.domain.repositories.BruteforceRepository
import net.typeblog.shelter.domain.repositories.FilesRepository
import net.typeblog.shelter.domain.repositories.LogsRepository
import net.typeblog.shelter.domain.repositories.PermissionsRepository
import net.typeblog.shelter.domain.repositories.ProfilesRepository
import net.typeblog.shelter.domain.repositories.RootRepository
import net.typeblog.shelter.domain.repositories.SettingsRepository
import net.typeblog.shelter.domain.repositories.UsbSettingsRepository
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