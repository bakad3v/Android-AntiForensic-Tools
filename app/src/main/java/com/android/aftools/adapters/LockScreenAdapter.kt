package com.android.aftools.adapters

import com.sonozaki.entities.LogsData
import com.sonozaki.lockscreen.domain.repository.LockScreenRepository
import com.sonozaki.logs.repository.LogsRepository
import com.sonozaki.password.repository.PasswordManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LockScreenAdapter @Inject constructor(
    private val logsRepository: LogsRepository,
    private val passwordManager: PasswordManager
): LockScreenRepository {
    override suspend fun checkPassword(password: CharArray): Boolean {
        return passwordManager.checkPassword(password)
    }

    override suspend fun writeToLogs(text: String) {
        logsRepository.writeToLogs(text)
    }

    override suspend fun getLogsData(): LogsData {
        return logsRepository.getLogsData().first()
    }
}