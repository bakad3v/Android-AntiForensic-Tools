package com.sonozaki.services.services

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sonozaki.services.R
import com.sonozaki.services.domain.usecases.GetLogsDataUseCase
import com.sonozaki.services.domain.usecases.GetRebootEnabledUseCase
import com.sonozaki.services.domain.usecases.WriteToLogsUseCase
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.superuser.superuser.SuperUserManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class DeviceRebootWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val superUserManager: SuperUserManager,
    private val getLogsDataUseCase: GetLogsDataUseCase,
    private val writeToLogsUseCase: WriteToLogsUseCase,
    private val getRebootEnabledUseCase: GetRebootEnabledUseCase
):
    CoroutineWorker(context, workerParams) {

    private suspend fun writeToLogs(entry: String) {
        if (getLogsDataUseCase().logsEnabled) {
            writeToLogsUseCase(entry)
        }
    }

    override suspend fun doWork(): Result {
        if (getRebootEnabledUseCase()) {
            try {
                writeToLogs(applicationContext.getString((R.string.reboot_started)))
                superUserManager.getSuperUser().reboot()
                return Result.success()
            } catch (e: SuperUserException) {
                writeToLogs(e.messageForLogs.asString(applicationContext))
                return Result.failure()
            }
        }
        return Result.success()
    }

    companion object {

        private const val NAME = "reboot_device"

        fun enqueueReboot(context: Context, delay: Int) {
            val work = OneTimeWorkRequestBuilder<DeviceRebootWorker>()
                .setInitialDelay(delay.toLong(), TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(NAME, ExistingWorkPolicy.REPLACE, work)
        }

        fun stopWorks(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(NAME)
        }
    }
}