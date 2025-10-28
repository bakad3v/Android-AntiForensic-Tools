package com.bakasoft.appupdatecenter.domain.usecases

import com.bakasoft.appinstaller.domain.repository.AppInstallerServiceRepository
import com.sonozaki.entities.AppInstallerData
import javax.inject.Inject

class SetAppInstallerDataUseCase @Inject constructor(
    private val repository: AppInstallerServiceRepository
) {
    suspend operator fun invoke(path: String, isTestOnly: Boolean, disableAdmin: Boolean) {
        repository.setInstallerData(AppInstallerData(path, isTestOnly, disableAdmin))
    }
}