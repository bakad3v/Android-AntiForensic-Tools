package net.typeblog.shelter.presentation.states

import net.typeblog.shelter.domain.entities.FileDomain


sealed class DeletionDataState: ClassWithProgressBar {
  data object Loading : DeletionDataState(), ShowProgressBar
  class ViewData(val items: List<FileDomain>) : DeletionDataState()
}
