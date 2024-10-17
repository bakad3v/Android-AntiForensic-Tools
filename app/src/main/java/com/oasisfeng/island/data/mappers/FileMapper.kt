package com.oasisfeng.island.data.mappers

import android.net.Uri
import com.oasisfeng.island.data.entities.FileDatastore
import com.oasisfeng.island.data.entities.FilesList
import com.oasisfeng.island.domain.entities.FileDomain
import javax.inject.Inject

class FileMapper @Inject constructor() {
  private fun mapDatastoreToDtModel(fileDatastore: FileDatastore) =
    FileDomain(
      size = fileDatastore.size,
      name = fileDatastore.name,
      priority = fileDatastore.priority,
      uri = Uri.parse(fileDatastore.uri),
      fileType = fileDatastore.fileType,
      sizeFormatted = fileDatastore.sizeFormatted
    )

  fun mapDatastoreListToDtList(list: FilesList): List<FileDomain> =
    list.list.map { mapDatastoreToDtModel(it) }
}
