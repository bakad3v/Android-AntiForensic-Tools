package net.typeblog.shelter.data.mappers

import android.net.Uri
import net.typeblog.shelter.data.entities.FileDatastore
import net.typeblog.shelter.data.entities.FilesList
import net.typeblog.shelter.domain.entities.FileDomain
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
