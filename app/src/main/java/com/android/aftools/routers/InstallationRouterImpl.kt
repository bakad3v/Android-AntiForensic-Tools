package com.android.aftools.routers

import android.content.Context
import com.bakasoft.appinstaller.service.AppInstallerWorker
import com.bakasoft.appupdatecenter.domain.AppUpdateRouter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InstallationRouterImpl @Inject constructor(
    @ApplicationContext private val context: Context
): AppUpdateRouter {
    override fun launchUpdate() {
        AppInstallerWorker.start(context)
    }

    override fun killNotifications() {
        AppInstallerWorker.stop(context)
    }
}