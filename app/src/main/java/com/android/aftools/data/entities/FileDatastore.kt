package com.android.aftools.data.entities

import com.android.aftools.domain.entities.FileType
import kotlinx.serialization.Serializable

@Serializable
data class FileDatastore(
    val size: Long,
    val name: String,
    val priority: Int,
    val uri: String,
    val fileType: FileType,
    val sizeFormatted: String)