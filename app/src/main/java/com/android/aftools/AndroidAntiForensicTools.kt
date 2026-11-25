package com.android.aftools

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.UserManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.core.content.ContextCompat
import com.android.aftools.domain.usecases.CheckTestOnlyStatus
import com.sonozaki.resources.IO_DISPATCHER
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class AndroidAntiForensicTools: Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    @Inject
    lateinit var checkTestOnlyStatus: CheckTestOnlyStatus

    @Inject
    @Named(IO_DISPATCHER)
    lateinit var dispatcher: CoroutineDispatcher

    @Inject
    lateinit var userManager: UserManager

    private val testOnlyCheckStarted = AtomicBoolean(false)
    private val applicationScope by lazy { CoroutineScope(SupervisorJob() + dispatcher) }
    private var userUnlockedReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        if (userManager.isUserUnlocked) {
            launchTestOnlyCheck()
        } else {
            registerUserUnlockedReceiver()
        }
    }

    private fun registerUserUnlockedReceiver() {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Intent.ACTION_USER_UNLOCKED) {
                    unregisterUserUnlockedReceiver()
                    launchTestOnlyCheck()
                }
            }
        }
        userUnlockedReceiver = receiver
        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter(Intent.ACTION_USER_UNLOCKED),
            ContextCompat.RECEIVER_EXPORTED
        )

        // Close the race where the user unlocks between the first check and registration.
        if (userManager.isUserUnlocked) {
            unregisterUserUnlockedReceiver()
            launchTestOnlyCheck()
        }
    }

    private fun unregisterUserUnlockedReceiver() {
        userUnlockedReceiver?.let { receiver ->
            runCatching { unregisterReceiver(receiver) }
        }
        userUnlockedReceiver = null
    }

    private fun launchTestOnlyCheck() {
        if (!testOnlyCheckStarted.compareAndSet(false, true)) {
            return
        }
        applicationScope.launch {
            checkTestOnlyStatus.runUntilComplete()
        }
    }


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
}
