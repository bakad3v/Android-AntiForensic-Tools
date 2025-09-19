package com.android.aftools.routers

import android.content.Context
import android.util.Log
import com.bakasoft.appinstaller.service.AppInstallerWorker
import com.bakasoft.appupdatecenter.domain.AppUpdateRouter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InstallationRouterImpl @Inject constructor(
    @ApplicationContext private val context: Context
): AppUpdateRouter {
    override fun launchUpdate() {
        Log.w("installation","start2")
        AppInstallerWorker.start(context)
    }
}