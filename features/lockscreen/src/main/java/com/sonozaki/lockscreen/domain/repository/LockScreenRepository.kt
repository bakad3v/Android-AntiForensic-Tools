package com.sonozaki.lockscreen.domain.repository

import com.sonozaki.entities.LogsData

interface LockScreenRepository {
    suspend fun checkPassword(password: CharArray): Boolean
    suspend fun writeToLogs(text: String)
    suspend fun getLogsData(): LogsData
}