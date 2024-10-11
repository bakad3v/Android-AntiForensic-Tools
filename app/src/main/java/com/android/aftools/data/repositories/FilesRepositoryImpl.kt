package com.android.aftools.data.repositories

import android.content.Context
import android.net.Uri
import androidx.datastore.dataStore
import androidx.documentfile.provider.DocumentFile
import com.android.aftools.data.entities.FileDatastore
import com.android.aftools.data.mappers.FileMapper
import com.android.aftools.data.serializers.FilesSerializer
import com.android.aftools.domain.entities.FileDomain
import com.android.aftools.domain.entities.FileType
import com.android.aftools.domain.entities.FilesSortOrder
import com.android.aftools.domain.repositories.FilesRepository
import com.anggrayudi.storage.file.getAbsolutePath
import com.anggrayudi.storage.file.mimeType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject


class FilesRepositoryImpl @Inject constructor(
  @ApplicationContext private val context: Context,
  private val mapper: FileMapper,
  private val sortOrderFlow: MutableStateFlow<FilesSortOrder>,
  filesSerializer: FilesSerializer
) : FilesRepository {


  override fun getSortOrder() = sortOrderFlow.asStateFlow()

  private val Context.filesDataStore by dataStore(DATASTORE_NAME,filesSerializer)


  @OptIn(ExperimentalCoroutinesApi::class)
  override fun getFilesDb() : Flow<List<FileDomain>> = sortOrderFlow.flatMapLatest {
    context.filesDataStore.data.map { files -> mapper.mapDatastoreListToDtList(files.getSorted(it)) }
  }


  override suspend fun clearDb() {
    context.filesDataStore.updateData {
      it.clear()
    }
  }


  override suspend fun changeFilePriority(priority: Int, uri: Uri) {
    context.filesDataStore.updateData {
      it.changePriority(uri.toString(),priority)
    }
  }


  override suspend fun changeSortOrder(sortOrder: FilesSortOrder) {
    sortOrderFlow.emit(sortOrder)
  }

  /**
   * Function for getting size of folder. Allows to get size of large folder rapidly using du command, can be buggy on latest versions of Android
   */
  private fun getFileSize(path: String): Long {
    val process = Runtime.getRuntime().exec(arrayOf("du", "-s", path))
    val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
    bufferedReader.use {
      val line = it.readLine()
      val result = try {
        line.split("\t")[0].toLong()
      } catch (e: Exception) {
        0
      }
      return result
    }
  }

  /**
   * Function for converting file size in bytes to human-readable format.
   */
  private fun Long.convertToHumanFormat(): String {
    var number = this
    val names = listOf("B","KB", "MB", "GB", "TB")
    var i = 0
    while (number > 1023) {
      number /= 1024
      i++
    }
    return "${number.toInt()} ${names[i]}"
  }


  override suspend fun insertMyFile(uri: Uri, isDirectory: Boolean) {
    val df = if(isDirectory) {
      DocumentFile.fromTreeUri(context,uri)
    } else {
      DocumentFile.fromSingleUri(context,uri)
    }?: throw RuntimeException("Can't get file or directory for uri $uri")
    val size = if (isDirectory) {
      getFileSize(df.getAbsolutePath(context)) * 1024
    } else {
      df.length()
    }
    val fileType = if (isDirectory) {
      FileType.DIRECTORY
    } else {
      if (df.mimeType?.startsWith("image/")==true) {
        FileType.IMAGE
      } else {
        FileType.USUAL_FILE
      }
    }
    context.filesDataStore.updateData {
      it.add(FileDatastore(uri = uri.toString(), name = df.name?:"No name",size = size, priority = 0, fileType = fileType, sizeFormatted = size.convertToHumanFormat()))
    }
  }


  override suspend fun deleteMyFile(uri: Uri) {
    context.filesDataStore.updateData {
      it.delete(uri.toString())
    }
  }

  companion object {
    private const val DATASTORE_NAME = "files_datastore.json"

  }

}
