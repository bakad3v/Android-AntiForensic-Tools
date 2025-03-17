package com.sonozaki.passwordsetup.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonozaki.passwordsetup.domain.usecases.SetPasswordUseCase
import com.sonozaki.passwordsetup.presentation.states.SetupPasswordState
import com.sonozaki.passwordstrength.PasswordStrengthCalculator
import com.sonozaki.resources.DEFAULT_DISPATCHER
import com.sonozaki.validators.BaseValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SetupPasswordVM @Inject constructor(
    private val setPasswordUseCase: SetPasswordUseCase,
    private val setupPasswordState: MutableSharedFlow<SetupPasswordState>,
    private val passwordStrengthCalculator: PasswordStrengthCalculator,
    @Named(DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    val passwordState = setupPasswordState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SetupPasswordState.PasswordStrengthCheck()
    )

    fun passwordChanged(password: String) {
        viewModelScope.launch(defaultDispatcher) {
            val passwordStrength = passwordStrengthCalculator.calculatePasswordStrength(password)
            setupPasswordState.emit(SetupPasswordState.PasswordStrengthCheck(passwordStrength))
        }
    }

    fun createPassword(password: CharArray) {
        viewModelScope.launch(defaultDispatcher) {
            val validationResult =
                BaseValidator.validate(
                    com.sonozaki.passwordsetup.presentation.validators.SimplePasswordValidator(
                        password.concatToString()
                    )
                )
            if (validationResult.isSuccess) {
                setPasswordUseCase(password)
            }
            setupPasswordState.emit(
                SetupPasswordState.CheckEnterPasswordResults(
                    passwordState.value.passwordStrength,
                    validationResult
                )
            )
        }
    }
}
