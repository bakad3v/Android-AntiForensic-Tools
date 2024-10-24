package com.android.aftools.presentation.receivers
import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import com.android.aftools.domain.usecases.bruteforce.OnRightPasswordUseCase
import com.android.aftools.domain.usecases.bruteforce.OnWrongPasswordUseCase
import com.android.aftools.domain.usecases.permissions.SetAdminActiveUseCase
import com.android.aftools.presentation.services.MyJobIntentService
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

    override fun onPasswordFailed(context: Context, intent: Intent, user: UserHandle) {
        coroutineScope.launch {
            if (onWrongPasswordUseCase()) {
                MyJobIntentService.start(context)
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
