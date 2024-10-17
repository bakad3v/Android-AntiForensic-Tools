package net.typeblog.shelter.domain.usecases.passwordManager

import net.typeblog.shelter.domain.repositories.PasswordManager
import javax.inject.Inject

class SetPasswordUseCase @Inject constructor(private val repository: PasswordManager) {
  suspend operator fun invoke(password: CharArray) {
    repository.setPassword(password)
  }
}
