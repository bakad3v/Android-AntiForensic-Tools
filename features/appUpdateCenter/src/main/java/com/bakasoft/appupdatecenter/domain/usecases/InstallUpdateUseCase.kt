package com.bakasoft.appupdatecenter.domain.usecases

import com.bakasoft.appupdatecenter.domain.AppUpdateRouter
import javax.inject.Inject

class InstallUpdateUseCase @Inject constructor(
    private val appUpdateRouter: AppUpdateRouter) {

    operator fun invoke() {
        appUpdateRouter.launchUpdate()
    }
}