package com.sonozaki.services.domain.entities

import android.net.Uri
import com.sonozaki.entities.FileType

data class FileDomain(
    val name: String,
    val priority: Int,
    val uri: Uri,
    val fileType: FileType)