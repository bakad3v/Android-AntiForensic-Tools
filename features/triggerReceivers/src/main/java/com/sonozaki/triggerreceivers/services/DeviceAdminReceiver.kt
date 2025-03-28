package com.sonozaki.triggerreceivers.services
import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import com.sonozaki.triggerreceivers.services.domain.router.ActivitiesLauncher
import com.sonozaki.triggerreceivers.services.domain.usecases.OnRightPasswordUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.OnWrongPasswordUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.SetAdminActiveUseCase
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

    override fun onPasswordFailed(context: Context, intent: Intent, user: UserHandle) {
        coroutineScope.launch {
            if (onWrongPasswordUseCase()) {
                activitiesLauncher.launchService(context)
            }
        }
        super.onPasswordFailed(context, intent, user)
    }

    override fun onPasswordSucceeded(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordSucceeded(context, intent, user)
        coroutineScope.launch {
            onRightPasswordUseCase()
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
