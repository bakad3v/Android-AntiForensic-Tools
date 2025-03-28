package com.android.aftools.di

import com.android.aftools.routers.LockScreenRouterImpl
import com.android.aftools.routers.PasswordSetupRouterImpl
import com.android.aftools.routers.ActivitiesLauncherImpl
import com.android.aftools.routers.SettingsRouterImpl
import com.android.aftools.routers.SplashRouterImpl
import com.sonozaki.lockscreen.domain.router.LockScreenRouter
import com.sonozaki.passwordsetup.domain.router.PasswordSetupRouter
import com.sonozaki.settings.domain.routers.SettingsRouter
import com.sonozaki.splash.domain.router.SplashRouter
import com.sonozaki.triggerreceivers.services.domain.router.ActivitiesLauncher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RoutersModule {

    @Binds
    @Singleton
    abstract fun bindServiceLauncher(launcherImpl: ActivitiesLauncherImpl): ActivitiesLauncher

    @Binds
    @Singleton
    abstract fun bindSplashRouter(routerImpl: SplashRouterImpl): SplashRouter

    @Binds
    @Singleton
    abstract fun bindSettingsRouter(routerImpl: SettingsRouterImpl): SettingsRouter

    @Binds
    @Singleton
    abstract fun bindPasswordSetupRouter(routerImpl: PasswordSetupRouterImpl): PasswordSetupRouter

    @Binds
    @Singleton
    abstract fun bindLockScreenRouter(routerImpl: LockScreenRouterImpl): LockScreenRouter
}