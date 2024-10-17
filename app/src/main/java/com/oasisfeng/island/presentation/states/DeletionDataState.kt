package com.oasisfeng.island.presentation.states

import com.oasisfeng.island.domain.entities.FileDomain


sealed class DeletionDataState: ClassWithProgressBar {
  data object Loading : DeletionDataState(), ShowProgressBar
  class ViewData(val items: List<FileDomain>) : DeletionDataState()
}
