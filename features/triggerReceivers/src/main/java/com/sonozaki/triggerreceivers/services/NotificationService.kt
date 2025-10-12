package com.sonozaki.triggerreceivers.services

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.sonozaki.entities.NotificationSettings
import com.sonozaki.resources.IO_DISPATCHER
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.superuser.superuser.SuperUserManager
import com.sonozaki.triggerreceivers.R
import com.sonozaki.triggerreceivers.services.domain.usecases.GetLogsEnabledUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.SetNotificationSettingsUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.WriteLogsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class NotificationService: NotificationListenerService() {

    @Inject
    lateinit var superUserManager: SuperUserManager

    @Inject
    @Named(IO_DISPATCHER)
    lateinit var dispatcher: CoroutineDispatcher

    @Inject
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var setNotificationSettingsUseCase: SetNotificationSettingsUseCase

    @Inject
    lateinit var getLogsEnabledUseCase: GetLogsEnabledUseCase

    @Inject
    lateinit var writeLogsUseCase: WriteLogsUseCase

    private suspend fun writeLogs(text: String) {
        if (getLogsEnabledUseCase()) {
            writeLogsUseCase(text)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        coroutineScope.launch(dispatcher) {
            setNotificationSettingsUseCase(NotificationSettings.ENABLED)
            writeLogs(baseContext.getString(R.string.notifications_interception_started))
        }
        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        coroutineScope.launch(dispatcher) {
            setNotificationSettingsUseCase(NotificationSettings.DISABLED)
            writeLogs(baseContext.getString(R.string.notifications_interception_stopped))
        }
        return super.onUnbind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val channel = sbn.notification.channelId
        val text = sbn.notification.extras.getString("android.text") ?: "null"
        if (channel == ACCESSIBILITY_SECURITY_POLICY && text.contains(applicationContext.applicationInfo.loadLabel(
            applicationContext.packageManager))) {
            coroutineScope.launch(dispatcher) {
                try {
                    writeLogs(baseContext.getString(
                        R.string.notification_intercepted, sbn.id, text
                    ))
                    superUserManager.getSuperUser().removeNotification(PACKAGE_NAME, sbn.id)
                    writeLogs(baseContext.getString(
                        R.string.notification_removed, sbn.id)
                    )
                } catch (e: SuperUserException) {
                    writeLogs(e.messageForLogs.asString(baseContext)
                    )
                } catch (e: Exception) {
                    writeLogs(baseContext.getString(
                        R.string.notification_removal_failed, sbn.id,
                        e.stackTraceToString())
                    )
                }
            }
        }
    }

    companion object {
        private const val ACCESSIBILITY_SECURITY_POLICY = "ACCESSIBILITY_SECURITY_POLICY"
        private const val PACKAGE_NAME = "android"
    }
}