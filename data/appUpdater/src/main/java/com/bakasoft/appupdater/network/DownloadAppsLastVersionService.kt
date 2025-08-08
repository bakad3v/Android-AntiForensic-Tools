package com.bakasoft.appupdater.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface DownloadAppsLastVersionService {
    @GET(VERSION_LINK)
    @Streaming
    suspend fun downloadAppLastVersion(): Response<ResponseBody>

    companion object {
        private const val VERSION_LINK = "https://raw.githubusercontent.com/bakad3v/Android-AntiForensic-Tools/refs/heads/master/app/build.gradle.kts"
    }
}