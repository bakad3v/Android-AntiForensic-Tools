package com.android.aftools.di

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.UserManager
import com.sonozaki.entities.ShizukuState
import com.sonozaki.triggerreceivers.services.DeviceAdminReceiver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AdminModule {
    @Provides
    @Singleton
    fun provideDevicePolicyManager(@ApplicationContext context: Context): DevicePolicyManager {
      return context.getSystemService(DevicePolicyManager::class.java)
    }

    @Provides
    @Singleton
    fun provideDeviceAdminReceiver(@ApplicationContext context: Context): ComponentName {
        return ComponentName(context, DeviceAdminReceiver::class.java)
    }

    @Provides
    @Singleton
    fun provideUserManager (@ApplicationContext context: Context): UserManager =
        context.getSystemService(UserManager::class.java)

    @Provides
    @Singleton
    fun provideShizukuStateFlow(): MutableStateFlow<ShizukuState> = MutableStateFlow(ShizukuState.LOADING)

}
