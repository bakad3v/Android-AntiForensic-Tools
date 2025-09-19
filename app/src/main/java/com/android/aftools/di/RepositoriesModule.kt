package com.android.aftools.di

import com.android.aftools.adapters.AppUpdaterAdapter
import com.android.aftools.adapters.SetupWizardRepositoryAdapter
import com.bakasoft.appinstaller.data.repository.AppInstallerRepositoryImpl
import com.bakasoft.appinstaller.domain.repository.AppInstallerServiceRepository
import com.bakasoft.appupdatecenter.domain.repository.AppUpdateCenterRepository
import com.bakasoft.appupdater.repository.AppUpdateRepository
import com.bakasoft.appupdater.repository.AppUpdateRepositoryImpl
import com.bakasoft.setupwizard.domain.repository.SetupWizardRepository
import com.sonozaki.data.files.repository.FilesRepository
import com.sonozaki.data.files.repository.FilesRepositoryImpl
import com.sonozaki.data.logs.repository.LogsRepositoryImpl
import com.sonozaki.data.profiles.repository.ProfilesRepository
import com.sonozaki.data.profiles.repository.ProfilesRepositoryImpl
import com.sonozaki.data.settings.repositories.BruteforceRepository
import com.sonozaki.data.settings.repositories.BruteforceSettingsRepositoryImpl
import com.sonozaki.data.settings.repositories.ButtonSettingsRepository
import com.sonozaki.data.settings.repositories.ButtonSettingsRepositoryImpl
import com.sonozaki.data.settings.repositories.PermissionsRepository
import com.sonozaki.data.settings.repositories.PermissionsRepositoryImpl
import com.sonozaki.data.settings.repositories.SettingsRepository
import com.sonozaki.data.settings.repositories.SettingsRepositoryImpl
import com.sonozaki.data.settings.repositories.USBSettingsRepositoryImpl
import com.sonozaki.data.settings.repositories.UsbSettingsRepository
import com.sonozaki.data.logs.repository.LogsRepository
import com.sonozaki.data.settings.repositories.DeviceProtectionSettingsRepository
import com.sonozaki.data.settings.repositories.DeviceProtectionSettingsRepositoryImpl
import com.sonozaki.root.repository.RootRepository
import com.sonozaki.root.repository.RootRepositoryImpl
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
    abstract fun bindAppUpdateRepository(impl: AppUpdateRepositoryImpl): AppUpdateRepository
    @Binds
    @Singleton
    abstract fun bindFilesRepository(filesRepositoryImpl: FilesRepositoryImpl): FilesRepository

    @Binds
    @Singleton
    abstract fun bindLogsRepository(filesRepositoryImpl: LogsRepositoryImpl): LogsRepository

    @Binds
    @Singleton
    abstract fun bindDeviceProtectionSettingsRepository(deviceProtectionSettingsRepositoryImpl: DeviceProtectionSettingsRepositoryImpl): DeviceProtectionSettingsRepository

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

    @Binds
    @Singleton
    abstract fun provideButtonRepository(buttonSettingsRepositoryImpl: ButtonSettingsRepositoryImpl): ButtonSettingsRepository

    @Binds
    @Singleton
    abstract fun provideAppUpdaterRepository(appUpdaterAdapter: AppUpdaterAdapter): AppUpdateCenterRepository

    @Binds
    @Singleton
    abstract fun provideAppInstallerRepository(appInstallerRepositoryImpl: AppInstallerRepositoryImpl): AppInstallerServiceRepository

    @Binds
    @Singleton
    abstract fun provideSetupWizardRepository(impl: SetupWizardRepositoryAdapter): SetupWizardRepository
}