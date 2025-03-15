package com.android.aftools.presentation.states

import com.android.aftools.domain.entities.FileDomain


sealed class DeletionDataState {
  data object Loading : DeletionDataState()
  class ViewData(val items: List<FileDomain>) : DeletionDataState()
}
