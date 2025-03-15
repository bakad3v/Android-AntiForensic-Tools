package com.android.aftools.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.android.aftools.domain.usecases.passwordManager.GetPasswordStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashVM @Inject constructor(
    getPasswordStatusUseCase: GetPasswordStatusUseCase
): ViewModel() {
    val passwordStatus = getPasswordStatusUseCase()
}