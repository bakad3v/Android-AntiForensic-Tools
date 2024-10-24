package com.android.aftools.domain.entities

import android.net.Uri

data class FileDomain(
    val size: Long,
    val name: String,
    val priority: Int,
    val uri: Uri,
    val fileType: FileType,
    val sizeFormatted: String)