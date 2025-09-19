package com.bakasoft.appinstaller.domain.repository

import com.bakasoft.network.RequestResult
import com.sonozaki.entities.AppInstallerData
import okhttp3.ResponseBody


interface AppInstallerServiceRepository {
    suspend fun getAppInstallatorData(): AppInstallerData
    fun installUsualApp(response: ResponseBody)
    suspend fun setInstallerData(data: AppInstallerData)
    suspend fun downloadUpdate(path: String): RequestResult<ResponseBody>
}