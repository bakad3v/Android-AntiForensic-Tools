package com.android.aftools.di

import com.android.aftools.data.repositories.BruteforceSettingsRepositoryImpl
import com.android.aftools.data.repositories.PermissionsRepositoryImpl
import com.android.aftools.data.repositories.SettingsRepositoryImpl
import com.android.aftools.data.repositories.USBSettingsRepositoryImpl
import com.android.aftools.domain.repositories.BruteforceRepository
import com.android.aftools.domain.repositories.PermissionsRepository
import com.android.aftools.domain.repositories.SettingsRepository
import com.android.aftools.domain.repositories.UsbSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {
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
}
