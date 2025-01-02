package com.oasisfeng.island.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oasisfeng.island.domain.usecases.settings.GetSettingsUseCase
import com.oasisfeng.island.presentation.states.ActivityState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
  private val _activityState: MutableStateFlow<ActivityState>,
  getSettingsUseCase: GetSettingsUseCase): ViewModel() {
  val activityState: StateFlow<ActivityState> get() = _activityState.asStateFlow()

  val theme = getSettingsUseCase().map { it.theme }

  fun setActivityState(state: ActivityState) {
    viewModelScope.launch {
      _activityState.emit(state)
    }
  }
}
