package net.typeblog.shelter.data.entities

import net.typeblog.shelter.domain.entities.FileType
import kotlinx.serialization.Serializable

@Serializable
data class FileDatastore(
    val size: Long,
    val name: String,
    val priority: Int,
    val uri: String,
    val fileType: FileType,
    val sizeFormatted: String)