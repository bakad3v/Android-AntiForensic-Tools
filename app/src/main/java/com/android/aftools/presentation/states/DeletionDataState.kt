package com.android.aftools.presentation.states

import com.android.aftools.domain.entities.FileDomain


sealed class DeletionDataState: ClassWithProgressBar {
  data object Loading : DeletionDataState(), ShowProgressBar
  class ViewData(val items: List<FileDomain>) : DeletionDataState()
}
