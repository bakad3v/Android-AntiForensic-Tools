package com.android.aftools.adapters

import com.sonozaki.entities.LogEntity
import com.sonozaki.entities.LogsData
import com.sonozaki.logs.domain.repository.LogsScreenRepository
import com.sonozaki.logs.repository.LogsRepository
import com.sonozaki.settings.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class LogsAdapter @Inject constructor(
    private val logsRepository: LogsRepository
): LogsScreenRepository {
    override val availableDays: Flow<Set<Long>>
        get() = logsRepository.getAvailableDays()
    override val logsData: Flow<LogsData>
        get() = logsRepository.getLogsData()
    override val logsText: Flow<LogEntity>
        get() = logsRepository.getLogsText()

    override suspend fun changeAutoDeletionTimeOut(timeout: Int) {
        logsRepository.changeAutoDeletionTimeOut(timeout)
    }

    override suspend fun changeLogsEnabled() {
        logsRepository.changeLogsEnabled()
    }

    override suspend fun clearLogsForDay(day: String) {
        logsRepository.clearLogsForDay(day)
    }

    override suspend fun lookLogsForDay(localDateTime: LocalDateTime) {
        logsRepository.lookLogsForDay(localDateTime)
    }
}