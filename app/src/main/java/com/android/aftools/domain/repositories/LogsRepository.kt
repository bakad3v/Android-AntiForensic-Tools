package com.android.aftools.domain.repositories

import com.android.aftools.domain.entities.LogEntity
import com.android.aftools.domain.entities.LogsData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository for storing app logs. Logs are encrypted and disabled by default.
 */
interface LogsRepository {

  /**
   * Function to clear logs for selected day
   */
  suspend fun clearLogsForDay(day: String)

  /**
   * Function to change logs auto deletion timeout
   */
  suspend fun changeAutoDeletionTimeOut(timeout: Int)

  /**
   * Function to write entry to logs
   */
  suspend fun writeToLogs(string: String)

  /**
   * Function to select logs for specified day
   */
  suspend fun lookLogsForDay(localDateTime: LocalDateTime)

  /**
   * Function to disable or enable logging
   */
  suspend fun changeLogsEnabled()
  /**
   * Function to get logs text
   */
  fun getLogsText(): Flow<LogEntity>
  /**
   * Function to get data about logs (are logs enabled, period of automatic clearing)
   */
  fun getLogsData(): Flow<LogsData>
  /**
   * Function for initializing logs DAO and clearing old logs
   */
  suspend fun init()
  /**
   * Function to get days for which logs are available
   */
  fun getAvailableDays(): Flow<Set<Long>>
}
