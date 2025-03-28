package com.android.aftools.mappers

import androidx.core.net.toUri
import com.sonozaki.data.files.entities.FileDatastore
import com.sonozaki.data.files.entities.FilesList
import com.sonozaki.files.domain.entities.FileInfo
import com.sonozaki.services.domain.entities.FileDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileMapper @Inject constructor() {
  private fun mapDatastoreToInfoModel(fileDatastore: FileDatastore) =
    FileInfo(
      size = fileDatastore.size,
      name = fileDatastore.name,
      priority = fileDatastore.priority,
      uri = fileDatastore.uri.toUri(),
      fileType = fileDatastore.fileType,
      sizeFormatted = fileDatastore.sizeFormatted
    )

  private fun mapDataStoreToDomainModel(fileDatastore: FileDatastore) = FileDomain(
    name = fileDatastore.name,
    priority = fileDatastore.priority,
    uri = fileDatastore.uri.toUri(),
    fileType = fileDatastore.fileType
  )

  fun mapDatastoreListToInfoList(list: FilesList): List<FileInfo> =
    list.list.map { mapDatastoreToInfoModel(it) }

  fun mapDatastoreListToDomainList(list: FilesList): List<FileDomain> = list.list.map {
    mapDataStoreToDomainModel(it)
  }
}
