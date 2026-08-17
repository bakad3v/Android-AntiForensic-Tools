package com.sonozaki.triggerreceivers.services

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.os.UserHandle
import com.sonozaki.entities.BruteforceDetectingMethod
import com.sonozaki.triggerreceivers.R
import com.sonozaki.triggerreceivers.services.domain.router.ActivitiesLauncher
import com.sonozaki.triggerreceivers.services.domain.usecases.GetBruteforceSettingsUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetLogsEnabledUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.OnRightPasswordUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.SetAdminActiveUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.WriteLogsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DeviceAdminReceiver: DeviceAdminReceiver() {

    @Inject
    lateinit var onRightPasswordUseCase: OnRightPasswordUseCase

    @Inject
    lateinit var devicePolicyManager: DevicePolicyManager

    @Inject
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var setAdminActiveUseCase: SetAdminActiveUseCase

    @Inject
    lateinit var activitiesLauncher: ActivitiesLauncher

    @Inject
    lateinit var getLogsEnabledUseCase: GetLogsEnabledUseCase

    @Inject
    lateinit var writeLogsUseCase: WriteLogsUseCase

    @Inject
    lateinit var getBruteforceSettingsUseCase: GetBruteforceSettingsUseCase

    private suspend fun writeLogsSafely(text: String) {
        try {
            if (getLogsEnabledUseCase()) {
                writeLogsUseCase(text)
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            // Logging is best-effort and must not prevent trigger processing.
        }
    }

    override fun onPasswordFailed(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordFailed(context, intent, user)
        if (user != Process.myUserHandle()) {
            return
        }

        // Capture the system-owned value while handling this exact broadcast. Reading it later
        // could make multiple queued callbacks observe the same newer attempt count.
        val failedAttempts = try {
            devicePolicyManager.currentFailedPasswordAttempts
        } catch (_: SecurityException) {
            // The administrator may have been disabled while this broadcast was being delivered.
            return
        }

        launchAsync {
            val settings = getBruteforceSettingsUseCase()
            if (settings.detectingMethod != BruteforceDetectingMethod.ADMIN) {
                return@launchAsync
            }

            val shouldTrigger = shouldTriggerAdminBruteforce(settings, failedAttempts)
            if (shouldTrigger) {
                // Enqueue the security-critical work before attempting best-effort logging.
                activitiesLauncher.launchService(context)
            }

            writeLogsSafely(context.getString(R.string.wrong_password_detected))
            if (shouldTrigger) {
                writeLogsSafely(context.getString(R.string.password_failed_reason))
            }
        }
    }

    override fun onPasswordSucceeded(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordSucceeded(context, intent, user)
        if (user != Process.myUserHandle()) {
            return
        }

        launchAsync {
            // Keep the fallback accessibility counter consistent across detection-mode changes.
            onRightPasswordUseCase()
        }
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        launchAsync {
            setAdminActiveUseCase(true)
        }
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        launchAsync {
            setAdminActiveUseCase(false)
        }
    }

    /**
     * Keeps the manifest receiver active until its short asynchronous work is complete.
     */
    private fun launchAsync(block: suspend () -> Unit) {
        val pendingResult = goAsync()
        coroutineScope.launch {
            try {
                block()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // A receiver callback must not crash the process. The pending result is still
                // completed below, and trigger work is durably enqueued before logging.
            }
        }.invokeOnCompletion {
            pendingResult.finish()
        }
    }
}
