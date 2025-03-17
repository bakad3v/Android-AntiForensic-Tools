package com.sonozaki.splash.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.sonozaki.splash.domain.usecases.GetPasswordStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashVM @Inject constructor(
    getPasswordStatusUseCase: GetPasswordStatusUseCase
): ViewModel() {
    val passwordStatus = getPasswordStatusUseCase()
}