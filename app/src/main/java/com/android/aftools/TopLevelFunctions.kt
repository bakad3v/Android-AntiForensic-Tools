package com.android.aftools

import android.content.Context
import android.util.TypedValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaLocalDateTime
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter

object TopLevelFunctions {
  fun CharArray.toByteArray(): ByteArray {
    val result = ByteArray(size)
    val buffer = CharBuffer.wrap(this)
    Charset.forName("UTF-8").encode(buffer).get(result)
    this.fill(Char(1))
    return result
  }

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
