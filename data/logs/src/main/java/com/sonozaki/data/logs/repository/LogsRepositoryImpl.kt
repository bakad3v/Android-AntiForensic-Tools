package com.sonozaki.data.logs.repository


import android.content.Context
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.data.logs.entities.LogDatastore
import com.sonozaki.data.logs.entities.LogList
import com.sonozaki.data.logs.mapper.LogMapper
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.entities.LogEntity
import com.sonozaki.entities.LogsData
import com.sonozaki.utils.TopLevelFunctions.getEpochDays
import com.sonozaki.utils.TopLevelFunctions.getMillis
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject


class LogsRepositoryImpl @Inject constructor(
  @ApplicationContext private val context: Context,
  private val dayFlow: MutableStateFlow<Long>,
  private val mapper: LogMapper,
  logsDataSerializer: BaseSerializer<LogsData>,
  logsSerializer: BaseSerializer<LogList>
) : LogsRepository {

  private val Context.logsDataStore by encryptedDataStore(
      ENTRIES_DATASTORE_NAME,
      logsSerializer,
      alias = EncryptionAlias.DATASTORE.name,
      isDBA = true
  )

  private val Context.logsDataDataStore by encryptedDataStore(
      DATASTORE_NAME,
      logsDataSerializer,
      alias = EncryptionAlias.DATASTORE.name,
      isDBA = true
  )


  @OptIn(ExperimentalCoroutinesApi::class)
  override fun getLogsText(): Flow<LogEntity> = dayFlow.flatMapLatest { day ->
    context.logsDataStore.data.map { it.getLogsForDay(day) }.map {  mapper.mapDataStoreToDt(it,dayFlow.value) }
  }


  override fun getLogsData(): Flow<LogsData> = context.logsDataDataStore.data


  override fun getAvailableDays() = context.logsDataStore.data.map { it.getAvailableDays() }


  override suspend fun init() {
      val availableDays = context.logsDataStore.data.first().getAvailableDays()
      val period = context.logsDataDataStore.data.first().logsAutoRemovePeriod
      val currentDay = dayFlow.value
      val toDelete = availableDays.filter { it < currentDay - period }.toSet()
      context.logsDataStore.updateData { it.deleteLogsForDays(toDelete) }
  }


  override suspend fun clearLogsForDay(day: String) {
    context.logsDataStore.updateData { it.deleteLogsForDays(setOf(LocalDate.parse(day).toEpochDay())) }
  }


  override suspend fun changeAutoDeletionTimeOut(timeout: Int) {
    context.logsDataDataStore.updateData {
      it.copy(logsAutoRemovePeriod = timeout)
    }
  }


  override suspend fun lookLogsForDay(localDateTime: LocalDateTime) {
    dayFlow.emit(localDateTime.getEpochDays())
  }


  override suspend fun changeLogsEnabled() {
    context.logsDataDataStore.updateData {
      it.copy(logsEnabled = !it.logsEnabled)
    }
  }

  override suspend fun writeToLogs(string: String) {
    val dateTime = LocalDateTime.now()
    val date = dateTime.getMillis()
    val day = dateTime.getEpochDays()
    context.logsDataStore.updateData {
     it.insertLogEntry(LogDatastore(id = it.list.size,date=date, day = day, entry = string))
    }
    if (day!=dayFlow.value) {
      dayFlow.emit(day)
    }
  }

  companion object {
    private const val DATASTORE_NAME = "logs_datastore.json"
    private const val ENTRIES_DATASTORE_NAME = "logs_entries_datastore.json"
  }
}
