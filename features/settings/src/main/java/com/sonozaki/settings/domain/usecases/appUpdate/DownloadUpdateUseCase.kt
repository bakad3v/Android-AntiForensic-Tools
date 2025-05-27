package com.sonozaki.settings.domain.usecases.appUpdate

import com.bakasoft.network.RequestResult
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import okhttp3.ResponseBody
import javax.inject.Inject

class DownloadUpdateUseCase @Inject constructor(val repository: SettingsScreenRepository) {
    suspend operator fun invoke(): RequestResult<ResponseBody> {
       return repository.downloadUpdate()
    }
}