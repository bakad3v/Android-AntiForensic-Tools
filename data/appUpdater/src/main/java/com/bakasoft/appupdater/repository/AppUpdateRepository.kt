package com.bakasoft.appupdater.repository

import com.bakasoft.network.RequestResult
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface AppUpdateRepository {
    val showUpdatePopupStatus: Flow<Boolean>
    suspend fun disableUpdatePopup()
    suspend fun downloadUpdate(): RequestResult<ResponseBody>
}