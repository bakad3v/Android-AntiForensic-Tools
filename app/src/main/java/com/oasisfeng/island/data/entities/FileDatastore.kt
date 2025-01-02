package com.oasisfeng.island.data.entities

import com.oasisfeng.island.domain.entities.FileType
import kotlinx.serialization.Serializable

@Serializable
data class FileDatastore(
    val size: Long,
    val name: String,
    val priority: Int,
    val uri: String,
    val fileType: FileType,
    val sizeFormatted: String)