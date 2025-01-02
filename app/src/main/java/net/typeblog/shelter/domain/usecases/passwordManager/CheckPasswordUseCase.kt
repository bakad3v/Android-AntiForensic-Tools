package net.typeblog.shelter.domain.usecases.passwordManager

import net.typeblog.shelter.domain.repositories.PasswordManager
import javax.inject.Inject

class CheckPasswordUseCase @Inject constructor(private val repository: PasswordManager) {
  suspend operator fun invoke(password: CharArray): Boolean {
    return repository.checkPassword(password)
  }
}
