package com.sonozaki.triggerreceivers.services
import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import com.sonozaki.entities.BruteforceDetectingMethod
import com.sonozaki.triggerreceivers.R
import com.sonozaki.triggerreceivers.services.domain.router.ActivitiesLauncher
import com.sonozaki.triggerreceivers.services.domain.usecases.GetBruteforceSettingsUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetLogsEnabledUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.OnRightPasswordUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.OnWrongPasswordUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.SetAdminActiveUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.WriteLogsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DeviceAdminReceiver: DeviceAdminReceiver() {

    @Inject
    lateinit var onRightPasswordUseCase: OnRightPasswordUseCase

    @Inject
    lateinit var onWrongPasswordUseCase: OnWrongPasswordUseCase

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

    private suspend fun writeLogs(text: String) {
        if (getLogsEnabledUseCase()) {
            writeLogsUseCase(text)
        }
    }

    override fun onPasswordFailed(context: Context, intent: Intent, user: UserHandle) {
        coroutineScope.launch {
            writeLogs(context.getString(R.string.wrong_password_detected))
            if (checkBruteforceDetectionMethod() && onWrongPasswordUseCase()) {
                writeLogs(context.getString(R.string.password_failed_reason))
                activitiesLauncher.launchService(context)
            }
        }
        super.onPasswordFailed(context, intent, user)
    }

    private suspend fun checkBruteforceDetectionMethod(): Boolean {
        return getBruteforceSettingsUseCase().detectingMethod == BruteforceDetectingMethod.ADMIN
    }

    override fun onPasswordSucceeded(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordSucceeded(context, intent, user)
        coroutineScope.launch {
            if (checkBruteforceDetectionMethod()) {
                onRightPasswordUseCase()
            }
        }
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        coroutineScope.launch {
            setAdminActiveUseCase(true)
        }
    }

    override fun onDisabled(context: Context, intent: Intent) {
        coroutineScope.launch {
            setAdminActiveUseCase(false)
        }
        super.onDisabled(context, intent)
    }
}
