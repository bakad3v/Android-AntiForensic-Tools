package com.sonozaki.files.repository

import android.net.Uri
import com.sonozaki.files.entities.FilesList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for storing info about usual user files. Data is encrypted.
 */
interface FilesRepository {
  /**
   * Clear list of files.
   */
  suspend fun clearFiles()

  /**
   * Change files sort order.
   */
  suspend fun changeSortOrder(sortOrder: com.sonozaki.entities.FilesSortOrder)

  /**
   * Setup file deletion priority. Files with lowest number would be deleted first.
   */
  suspend fun changeFilePriority(priority: Int, uri: Uri)

  /**
   * Function for file inserting. Gets size of file and converts it to human format, sets default priority to 0, determines the file type.
   */
  suspend fun insertMyFile(uri: Uri, isDirectory: Boolean)

  /**
   * Delete file from repository.
   */
  suspend fun deleteMyFile(uri: Uri)

  /**
   * Retrieve files data
   */
  fun getFiles(): Flow<FilesList>

  /**
   * Get files sort order
   */
  fun getSortOrder(): StateFlow<com.sonozaki.entities.FilesSortOrder>
}
