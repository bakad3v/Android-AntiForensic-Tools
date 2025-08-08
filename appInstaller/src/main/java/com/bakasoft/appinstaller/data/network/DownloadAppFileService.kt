package com.bakasoft.appinstaller.data.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadAppFileService {

    @GET
    @Streaming
    suspend fun downloadAppTestVersion(@Url url: String): Response<ResponseBody>

}