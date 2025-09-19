package com.android.aftools.di

import com.android.aftools.BuildConfig
import com.bakasoft.appinstaller.data.network.DownloadAppFileService
import com.bakasoft.appupdater.network.DownloadAppsLastVersionService
import com.bakasoft.network.RequestResult
import com.sonozaki.entities.AppInstallerData
import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.resources.APP_FLAVOR
import com.sonozaki.resources.APP_VERSION
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableSharedFlow
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UpdateModule {

    @Provides
    @Singleton
    @Named(APP_VERSION)
    fun getAppVersion(): String = BuildConfig.VERSION_NAME

    @Provides
    @Singleton
    @Named(APP_FLAVOR)
    fun getAppFlavor(): String = BuildConfig.FLAVOR_name


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(GITHUB).addConverterFactory(
            ScalarsConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    fun provideUpdateService(retrofit: Retrofit): DownloadAppFileService {
       return retrofit.create(DownloadAppFileService::class.java)
    }

    @Provides
    @Singleton
    fun provideDownloadLastVersionService(retrofit: Retrofit): DownloadAppsLastVersionService {
        return retrofit.create(DownloadAppsLastVersionService::class.java)
    }

    @Provides
    @Singleton
    fun provideLatestVersionSharedFlow(): MutableSharedFlow<RequestResult<AppLatestVersion>> =
        MutableSharedFlow(replay = 1)

    @Provides
    @Singleton
    fun provideAppinstallatorDataSharedFlow(): MutableSharedFlow<AppInstallerData> =
        MutableSharedFlow(replay = 1)

    companion object {
        const val GITHUB = "https://github.com/"
    }
}