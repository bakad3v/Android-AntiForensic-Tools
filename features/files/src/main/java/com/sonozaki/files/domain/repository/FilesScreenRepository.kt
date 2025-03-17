package com.sonozaki.files.domain.repository

import android.net.Uri
import com.sonozaki.entities.FilesSortOrder
import com.sonozaki.files.domain.entities.FileInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FilesScreenRepository {
    val deletionEnabled: Flow<Boolean>
    val sortOrder: StateFlow<FilesSortOrder>
    val files: Flow<List<FileInfo>>
    suspend fun changeFilePriority(priority: Int, uri: Uri)
    suspend fun changeSortOrder(sortOrder: FilesSortOrder)
    suspend fun insertMyFile(uri: Uri, isDirectory: Boolean)
    suspend fun clearFiles()
    suspend fun deleteMyFile(uri: Uri)
    suspend fun setDeleteFiles(new: Boolean)
}