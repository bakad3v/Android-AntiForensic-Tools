package com.android.aftools.di

import com.android.aftools.adapters.FilesAdapter
import com.android.aftools.adapters.LockScreenAdapter
import com.android.aftools.adapters.LogsAdapter
import com.android.aftools.adapters.MainActivityAdapter
import com.android.aftools.adapters.PasswordSetupAdapter
import com.android.aftools.adapters.ProfilesAdapter
import com.android.aftools.adapters.ReceiversAdapter
import com.android.aftools.adapters.RootAdapter
import com.android.aftools.adapters.ServicesAdapter
import com.android.aftools.adapters.SettingsAdapter
import com.android.aftools.adapters.SplashAdapter
import com.android.aftools.domain.repository.MainActivityRepository
import com.sonozaki.files.domain.repository.FilesScreenRepository
import com.sonozaki.lockscreen.domain.repository.LockScreenRepository
import com.sonozaki.logs.domain.repository.LogsScreenRepository
import com.sonozaki.passwordsetup.domain.repository.PasswordSetupRepository
import com.sonozaki.profiles.domain.repository.ProfilesScreenRepository
import com.sonozaki.rootcommands.domain.repository.RootScreenRepository
import com.sonozaki.services.domain.repository.ServicesRepository
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import com.sonozaki.splash.domain.repository.SplashRepository
import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdaptersModule {
    @Binds
    @Singleton
    abstract fun bindReceiversAdapter(receiversAdapter: ReceiversAdapter): ReceiversRepository

    @Binds
    @Singleton
    abstract fun bindSplashAdapter(splashAdapter: SplashAdapter): SplashRepository

    @Binds
    @Singleton
    abstract fun bindSettingsAdapter(settingsAdapter: SettingsAdapter): SettingsScreenRepository

    @Binds
    @Singleton
    abstract fun bindServicesAdapter(servicesAdapter: ServicesAdapter): ServicesRepository

    @Binds
    @Singleton
    abstract fun bindRootAdapter(rootAdapter: RootAdapter): RootScreenRepository

    @Binds
    @Singleton
    abstract fun bindProfilesAdapter(profilesAdapter: ProfilesAdapter): ProfilesScreenRepository

    @Binds
    @Singleton
    abstract fun bindPasswordSetupAdapter(passwordSetupAdapter: PasswordSetupAdapter): PasswordSetupRepository

    @Binds
    @Singleton
    abstract fun bindLogsAdapter(logsAdapter: LogsAdapter): LogsScreenRepository

    @Binds
    @Singleton
    abstract fun bindLockScreenAdapter(lockScreenAdapter: LockScreenAdapter): LockScreenRepository

    @Binds
    @Singleton
    abstract fun bindFilesAdapter(filesAdapter: FilesAdapter): FilesScreenRepository

    @Binds
    @Singleton
    abstract fun bindMainScreenAdapter(mainActivityAdapter: MainActivityAdapter): MainActivityRepository
}