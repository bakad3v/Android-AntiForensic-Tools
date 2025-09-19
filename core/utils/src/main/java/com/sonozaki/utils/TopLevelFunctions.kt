package com.sonozaki.utils

import android.content.Context
import android.util.TypedValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bakasoft.network.NetworkError
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

  fun networkErrorToText(error: NetworkError): UIText.StringResource {
    return when(error) {
      is NetworkError.EmptyResponse -> UIText.StringResource(R.string.empty_response)
      is NetworkError.ConnectionError -> UIText.StringResource(R.string.connection_error)
      is NetworkError.ServerError -> UIText.StringResource(R.string.server_error, error.code, error.description)
      is NetworkError.UnknownError -> UIText.StringResource(R.string.unknown_error, error.error)
    }
  }

  fun Context.getColorForAttribute(attributeId: Int): Int {
    val color = TypedValue()
    this.theme?.resolveAttribute(attributeId,color,true)
    return color.resourceId
  }
}
