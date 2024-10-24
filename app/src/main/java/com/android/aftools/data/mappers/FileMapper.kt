package com.android.aftools.data.mappers

import android.net.Uri
import com.android.aftools.data.entities.FileDatastore
import com.android.aftools.data.entities.FilesList
import com.android.aftools.domain.entities.FileDomain
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
