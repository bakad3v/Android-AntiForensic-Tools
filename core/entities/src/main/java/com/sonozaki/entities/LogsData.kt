package com.sonozaki.entities

import kotlinx.serialization.Serializable


@Serializable
data class LogsData( val logsEnabled: Boolean=false,val logsAutoRemovePeriod: Int = 7)
