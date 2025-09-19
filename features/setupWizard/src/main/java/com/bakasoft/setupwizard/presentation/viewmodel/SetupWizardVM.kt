package com.bakasoft.setupwizard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakasoft.setupwizard.domain.entities.SetupWizardState
import com.bakasoft.setupwizard.domain.usecases.GetWizardStateUseCase
import com.bakasoft.setupwizard.domain.usecases.LoadDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SetupWizardVM @Inject constructor(
    getWizardStateUseCase: GetWizardStateUseCase,
    private val loadDataUseCase: LoadDataUseCase,
): ViewModel() {


    val wizardSate = getWizardStateUseCase()
        .stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
        SetupWizardState.Loading
    ).onSubscription {
        loadDataUseCase()
    }

}