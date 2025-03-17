package com.sonozaki.logs.mapper

import com.sonozaki.entities.LogEntity
import com.sonozaki.logs.entities.LogList
import com.sonozaki.utils.TopLevelFunctions.formatTime
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class LogMapper @Inject constructor() {
  fun mapDataStoreToDt(logs: LogList, day: Long): LogEntity {
    val logsFormatted = logs.list.joinToString("\n") {
      val time =  kotlinx.datetime.Instant.fromEpochMilliseconds(it.date).toLocalDateTime(
        kotlinx.datetime.TimeZone.currentSystemDefault()
      ).formatTime()
      "<span style=\"color: %s;\">$time</span> <span style=\"color: %s;\">${it.entry}</span><br>"
    }
    return LogEntity(
        LocalDateTime.of(
            LocalDate.ofEpochDay(day),
            LocalTime.now()
        ), logsFormatted
    )
  }
}
