package com.sonozaki.files.presentation.state

import com.sonozaki.files.domain.entities.FileInfo


sealed class FileDataState {
  data object Loading : FileDataState()
  class ViewData(val items: List<FileInfo>) : FileDataState()
}
