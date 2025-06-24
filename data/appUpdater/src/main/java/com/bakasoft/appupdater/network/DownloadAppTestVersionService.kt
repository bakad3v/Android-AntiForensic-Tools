package com.bakasoft.appupdater.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadAppTestVersionService {

    @GET
    @Streaming
    suspend fun downloadAppTestVersion(@Url url: String): Response<ResponseBody>

}