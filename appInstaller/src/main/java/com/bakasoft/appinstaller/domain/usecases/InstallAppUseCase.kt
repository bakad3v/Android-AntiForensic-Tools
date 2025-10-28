package com.bakasoft.appinstaller.domain.usecases

import com.bakasoft.appinstaller.domain.repository.AppInstallerServiceRepository
import com.bakasoft.network.NetworkError
import com.bakasoft.network.RequestResult
import com.sonozaki.superuser.superuser.SuperUserManager
import okhttp3.ResponseBody
import javax.inject.Inject

class InstallAppUseCase @Inject constructor(
    private val appInstallerRepository: AppInstallerServiceRepository,
    private val superUserManager: SuperUserManager
){
    suspend operator fun invoke(): NetworkError? {
        val data = appInstallerRepository.getAppInstallatorData()
        val result = appInstallerRepository.downloadUpdate(data.path)
        return when (result) {
            is RequestResult.Data<ResponseBody> -> {
                if (data.disableAdmin) {
                    try {
                        superUserManager.removeAdminRights()
                    } catch (e: Exception) {}
                }
                if (data.isTestOnly) {
                    try {
                        superUserManager.getSuperUser().installTestOnlyApp(
                            result.data.contentLength(),
                            result.data.source()
                        )
                        null
                    } catch (e: Exception) {
                        NetworkError.UnknownError(e.stackTraceToString())
                    }
                } else {
                    try {
                        appInstallerRepository.installUsualApp(result.data)
                        null
                    } catch (e: Exception) {
                        NetworkError.UnknownError(e.stackTraceToString())
                    }
                }
            }
            is RequestResult.Error -> {
                result.error
            }
        }
    }
}