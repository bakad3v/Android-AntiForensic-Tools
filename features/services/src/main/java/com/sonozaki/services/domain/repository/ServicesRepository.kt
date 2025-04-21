package com.sonozaki.services.domain.repository

import android.net.Uri
import com.sonozaki.entities.App
import com.sonozaki.entities.LogsData
import com.sonozaki.entities.Settings
import com.sonozaki.services.domain.entities.FileDomain

interface ServicesRepository {

    suspend fun writeToLogs(text: String)

    suspend fun getLogsData(): LogsData

    suspend fun removeProfileFromDeletion(profile: Int)

    fun getManagedApps(): List<App>

    suspend fun getRebootEnabled(): Boolean

    suspend fun getRootCommand(): String

    suspend fun getProfilesToDelete(): List<Int>

    suspend fun getSettings(): Settings

    suspend fun getFiles(): List<FileDomain>

    suspend fun deleteMyFile(uri: Uri)

    suspend fun setRunOnBoot()

    suspend fun removeApplication(packageName: String)
}