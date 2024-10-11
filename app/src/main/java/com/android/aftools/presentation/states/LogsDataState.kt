package com.android.aftools.presentation.states

import com.android.aftools.presentation.utils.UIText
import java.time.LocalDateTime

sealed class LogsDataState(open val date: LocalDateTime) {

  class Loading(override val date: LocalDateTime = LocalDateTime.now()) :
    LogsDataState(date)

  class ViewLogs(override val date: LocalDateTime, val logs: UIText.ColoredHTMLText) :
    LogsDataState(date)

}
