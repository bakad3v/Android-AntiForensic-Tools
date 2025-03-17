package com.android.aftools.routers

import android.content.Context
import com.sonozaki.services.services.AFUActivitiesRunner
import com.sonozaki.services.services.BFUActivitiesRunner
import com.sonozaki.services.services.MyJobIntentService
import com.sonozaki.triggerreceivers.services.domain.router.ActivitiesLauncher
import javax.inject.Inject

class ActivitiesLauncherImpl @Inject constructor(
    private val bfuActivitiesRunner: BFUActivitiesRunner,
    private val afuActivitiesRunner: AFUActivitiesRunner
) : ActivitiesLauncher {
    override fun launchService(context: Context) {
        MyJobIntentService.start(context)
    }

    override suspend fun startBFU() {
        bfuActivitiesRunner.runTask()
    }

    override suspend fun startAFU() {
        afuActivitiesRunner.runTask()
    }
}