package com.android.aftools.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.settings.domain.usecases.settings.GetSettingsUseCase
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
    getSettingsUseCase: GetSettingsUseCase
): ViewModel() {
  val activityState: StateFlow<ActivityState> get() = _activityState.asStateFlow()

  val uiSettings = getSettingsUseCase().map { it.uiSettings }

  fun setActivityState(state: ActivityState) {
    viewModelScope.launch {
      _activityState.emit(state)
    }
  }
}
