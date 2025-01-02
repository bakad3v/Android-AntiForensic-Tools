package net.typeblog.shelter.domain.usecases.passwordManager

import net.typeblog.shelter.domain.repositories.PasswordManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPasswordStatusUseCase @Inject constructor(private val repository:PasswordManager) {
  operator fun invoke(): Flow<Boolean> {
    return repository.passwordStatus
  }
}
