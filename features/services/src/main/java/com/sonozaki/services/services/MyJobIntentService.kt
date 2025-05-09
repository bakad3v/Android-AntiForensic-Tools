package com.sonozaki.services.services

import android.content.Context
import android.content.Intent
import android.os.UserManager
import androidx.core.app.JobIntentService
import com.sonozaki.resources.AFU_RUNNER
import com.sonozaki.resources.BFU_RUNNER
import com.sonozaki.services.domain.usecases.SetRunOnBootUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MyJobIntentService: JobIntentService() {

    @Inject lateinit var coroutineScope: CoroutineScope

    @Inject
    @Named(BFU_RUNNER)
    lateinit var runnerBFU: ActivityRunner

    @Inject
    @Named(AFU_RUNNER)
    lateinit var runnerAFU: ActivityRunner

    @Inject lateinit var setRunOnBootUseCase: SetRunOnBootUseCase
    @Inject lateinit var userManager: UserManager
    companion object {
        private const val JOB_ID = 1
        fun start(context: Context) {
            enqueueWork(context, MyJobIntentService::class.java, JOB_ID, Intent())
        }
    }

    /**
     * Triggers actions that can be started before the device is unlocked. If the device remains locked, it postpones other actions until unlocked, otherwise it performs them immediately.
     */
    override fun onHandleWork(intent: Intent) {
        runBlocking {
            runnerBFU.runTask()
            if (userManager.isUserUnlocked) {
                runnerAFU.runTask()
            } else
                setRunOnBootUseCase()
        }
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }
}