package com.sonozaki.files.domain.entities

import android.net.Uri
import com.sonozaki.entities.FileType

data class FileInfo(
    val size: Long,
    val name: String,
    val priority: Int,
    val uri: Uri,
    val fileType: FileType,
    val sizeFormatted: String)