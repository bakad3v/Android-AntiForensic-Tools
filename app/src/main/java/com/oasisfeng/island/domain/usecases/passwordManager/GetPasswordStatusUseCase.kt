package com.oasisfeng.island.domain.usecases.passwordManager

import com.oasisfeng.island.domain.repositories.PasswordManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPasswordStatusUseCase @Inject constructor(private val repository:PasswordManager) {
  operator fun invoke(): Flow<Boolean> {
    return repository.passwordStatus
  }
}
