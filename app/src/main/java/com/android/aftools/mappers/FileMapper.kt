package com.android.aftools.mappers

import android.net.Uri
import com.sonozaki.files.domain.entities.FileInfo
import com.sonozaki.files.entities.FileDatastore
import com.sonozaki.files.entities.FilesList
import com.sonozaki.services.domain.entities.FileDomain
import javax.inject.Inject

class FileMapper @Inject constructor() {
  private fun mapDatastoreToInfoModel(fileDatastore: FileDatastore) =
    FileInfo(
      size = fileDatastore.size,
      name = fileDatastore.name,
      priority = fileDatastore.priority,
      uri = Uri.parse(fileDatastore.uri),
      fileType = fileDatastore.fileType,
      sizeFormatted = fileDatastore.sizeFormatted
    )

  private fun mapDataStoreToDomainModel(fileDatastore: FileDatastore) = FileDomain(
    name = fileDatastore.name,
    priority = fileDatastore.priority,
    uri = Uri.parse(fileDatastore.uri),
    fileType = fileDatastore.fileType
  )

  fun mapDatastoreListToInfoList(list: FilesList): List<FileInfo> =
    list.list.map { mapDatastoreToInfoModel(it) }

  fun mapDatastoreListToDomainList(list: FilesList): List<FileDomain> = list.list.map {
    mapDataStoreToDomainModel(it)
  }
}
