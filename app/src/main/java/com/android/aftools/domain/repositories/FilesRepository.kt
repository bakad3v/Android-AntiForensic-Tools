package com.android.aftools.domain.repositories

import android.net.Uri
import com.android.aftools.domain.entities.FileDomain
import com.android.aftools.domain.entities.FilesSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for storing info about usual user files. Data is encrypted.
 */
interface FilesRepository {
  /**
   * Clear list of files.
   */
  suspend fun clearDb()

  /**
   * Change files sort order.
   */
  suspend fun changeSortOrder(sortOrder: FilesSortOrder)

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
  fun getFilesDb(): Flow<List<FileDomain>>

  /**
   * Get files sort order
   */
  fun getSortOrder(): StateFlow<FilesSortOrder>
}
