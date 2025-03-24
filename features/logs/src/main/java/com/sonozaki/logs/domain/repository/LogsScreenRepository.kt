package com.sonozaki.logs.domain.repository

import com.sonozaki.entities.LogEntity
import com.sonozaki.entities.LogsData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface LogsScreenRepository {
    val availableDays: Flow<Set<Long>>
    val logsData: Flow<LogsData>
    val logsText: Flow<LogEntity>
    suspend fun changeAutoDeletionTimeOut(timeout: Int)
    suspend fun changeLogsEnabled()
    suspend fun clearLogsForDay(day: String)
    suspend fun lookLogsForDay(localDateTime: LocalDateTime)
}