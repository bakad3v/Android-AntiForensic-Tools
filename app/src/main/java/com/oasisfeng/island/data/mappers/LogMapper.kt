package com.oasisfeng.island.data.mappers

import com.oasisfeng.island.TopLevelFunctions.formatTime
import com.oasisfeng.island.data.entities.LogList
import com.oasisfeng.island.domain.entities.LogEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

import javax.inject.Inject

class LogMapper @Inject constructor() {
  fun mapDataStoreToDt(logs: LogList, day: Long): LogEntity {
    val logsFormatted = logs.list.joinToString("\n") {
      val time =  Instant.fromEpochMilliseconds(it.date).toLocalDateTime(
        TimeZone.currentSystemDefault()
      ).formatTime()
      "<span style=\"color: %s;\">$time</span> <span style=\"color: %s;\">${it.entry}</span><br>"
    }
    return LogEntity(LocalDateTime.of(LocalDate.ofEpochDay(day), LocalTime.now()), logsFormatted)
  }
}
