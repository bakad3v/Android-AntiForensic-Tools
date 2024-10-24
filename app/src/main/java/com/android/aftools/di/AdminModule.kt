package com.android.aftools.di

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.UserManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun provideUserManager (@ApplicationContext context: Context): UserManager =
        context.getSystemService(UserManager::class.java)


}
