package com.android.aftools.di

import com.android.aftools.BuildConfig
import com.bakasoft.appupdater.network.DownloadAppTestVersionService
import com.sonozaki.resources.APP_UPDATE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UpdateModule {

    @Provides
    @Singleton
    @Named(APP_UPDATE_URL)
    fun getUrl(): String {
        val version = when (BuildConfig.FLAVOR_name) {
            "island" -> "AFTools_island_TESTONLY.apk"
            "shelter" -> "AFTools_shelter_TESTONLY.apk"
            else -> return ""
        }
        return  "bakad3v/Android-AntiForensic-Tools/releases/download/v${BuildConfig.VERSION_NAME}/$version"
    }


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(GITHUB).addConverterFactory(
            ScalarsConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    fun provideUpdateService(retrofit: Retrofit): DownloadAppTestVersionService {
       return retrofit.create(DownloadAppTestVersionService::class.java)
    }

    companion object {
        const val GITHUB = "https://github.com/"
    }
}