package com.android.aftools

import android.content.Context
import android.util.TypedValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaLocalDateTime
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter

object TopLevelFunctions {

  fun LocalDateTime.formatDate(): String = this.toLocalDate().toString()

  fun kotlinx.datetime.LocalDateTime.formatTime(): String = this.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("hh:mm:ss"))

  fun LocalDateTime.getMillis() = this.atZone(systemDefault()).toInstant().toEpochMilli()

  fun LocalDateTime.getEpochDays() = this.toLocalDate().toEpochDay()

  fun Long.milliSecondsToDays() = LocalDateTime.ofEpochSecond(this/1000,0, OffsetDateTime.now().offset).getEpochDays()

  fun LifecycleOwner.launchLifecycleAwareCoroutine(coroutine: suspend () -> Unit) {
    with(this) {
      this.lifecycleScope.launch {
        this@with.repeatOnLifecycle(Lifecycle.State.STARTED) {
          coroutine()
        }
      }
    }
  }

  fun Context.getColorForAttribute(attributeId: Int): Int {
    val color = TypedValue()
    this.theme?.resolveAttribute(attributeId,color,true)
    return color.resourceId
  }
}
