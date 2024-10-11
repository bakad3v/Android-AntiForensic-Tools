package com.android.aftools.domain.usecases.passwordManager

import com.android.aftools.domain.repositories.PasswordManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPasswordStatusUseCase @Inject constructor(private val repository:PasswordManager) {
  operator fun invoke(): Flow<Boolean> {
    return repository.passwordStatus
  }
}
