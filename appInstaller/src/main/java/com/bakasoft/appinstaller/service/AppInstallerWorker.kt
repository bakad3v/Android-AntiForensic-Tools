package com.bakasoft.appinstaller.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bakasoft.appinstaller.R
import com.bakasoft.appinstaller.domain.usecases.InstallAppUseCase
import com.bakasoft.network.NetworkError
import com.sonozaki.resources.IO_DISPATCHER
import com.sonozaki.utils.TopLevelFunctions.networkErrorToText
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Named

@HiltWorker
class AppInstallerWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val installAppUseCase: InstallAppUseCase,
    @Named(IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
): CoroutineWorker(context, workerParams) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    override suspend fun doWork(): Result {
        createNotificationChannel()
        setForegroundAsync(createForegroundInfo(context.getString(R.string.app_update_in_progress)))
        return withContext(dispatcher) {
            val result = installAppUseCase()
            showFinishedNotification(result)
            if (result == null) {
                Result.success()
            } else {
                Result.failure()
            }
        }
    }

    private fun createForegroundInfo(title: String): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.baseline_download_24)
            .setOngoing(true)
            .setProgress(0, 0, true)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    private fun showFinishedNotification(networkError: NetworkError? = null) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(if (networkError == null) context.getString(R.string.update_success) else context.getString(R.string.update_failed) + " " + networkErrorToText(networkError).asString(context))
            .setSmallIcon(if (networkError == null) R.drawable.outline_download_done_24 else R.drawable.outline_error_24)
            .setAutoCancel(true)
            .build()
        notificationManager.cancel(NOTIFICATION_ID)
        notificationManager.notify(NOTIFICATION_ID + 1, notification)
    }

    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.app_installation),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.app_installation_description)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }


    companion object {
        private const val CHANNEL_ID = "app_installer_channel"
        private const val NOTIFICATION_ID = 1001
        private const val WORK_NAME = "app_installer"

        fun start(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<AppInstallerWorker>()
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
        }

        fun stop(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}