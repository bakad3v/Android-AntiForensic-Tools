package com.android.aftools.routers

import android.content.Context
import com.sonozaki.resources.AFU_RUNNER
import com.sonozaki.resources.BFU_RUNNER
import com.sonozaki.services.services.ActivityRunner
import com.sonozaki.services.services.MyJobIntentService
import com.sonozaki.triggerreceivers.services.domain.router.ActivitiesLauncher
import javax.inject.Inject
import javax.inject.Named

class ActivitiesLauncherImpl @Inject constructor(
    @Named(BFU_RUNNER) private val bfuActivitiesRunner: ActivityRunner,
    @Named(AFU_RUNNER) private val afuActivitiesRunner: ActivityRunner
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