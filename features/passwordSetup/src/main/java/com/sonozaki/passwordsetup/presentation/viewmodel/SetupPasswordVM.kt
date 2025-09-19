package com.sonozaki.passwordsetup.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonozaki.passwordsetup.domain.usecases.SetPasswordUseCase
import com.sonozaki.passwordsetup.presentation.actions.PasswordActions
import com.sonozaki.passwordsetup.presentation.states.SetupPasswordState
import com.sonozaki.passwordsetup.presentation.validators.SimplePasswordValidator
import com.sonozaki.passwordstrength.PasswordStrengthCalculator
import com.sonozaki.resources.DEFAULT_DISPATCHER
import com.sonozaki.validators.BaseValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SetupPasswordVM @Inject constructor(
    private val setPasswordUseCase: SetPasswordUseCase,
    private val setupPasswordState: MutableSharedFlow<SetupPasswordState>,
    private val passwordStrengthCalculator: PasswordStrengthCalculator,
    @Named(DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher,
    private val passwordCreatedChannel: Channel<PasswordActions>
) : ViewModel() {

    val passwordCreatedFlow = passwordCreatedChannel.receiveAsFlow()

    val passwordState = setupPasswordState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
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
                    SimplePasswordValidator(
                        password.concatToString()
                    )
                )
            if (validationResult.isSuccess) {
                setPasswordUseCase(password)
                passwordCreatedChannel.send(PasswordActions.PASSWORD_CREATED)
            } else {
                setupPasswordState.emit(
                    SetupPasswordState.DisplayError(
                        passwordState.value.passwordStrength,
                        validationResult.message
                    )
                )
            }
        }
    }
}
