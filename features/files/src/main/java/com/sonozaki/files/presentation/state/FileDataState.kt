package com.sonozaki.files.presentation.state

import com.sonozaki.files.domain.entities.FileInfo


sealed class FileDataState(open val expandedFABS: Boolean) {
  data class Loading(override val expandedFABS: Boolean = false): FileDataState(expandedFABS)
  data class ViewData(val items: List<FileInfo>, override val expandedFABS: Boolean = false) : FileDataState(expandedFABS)
}
