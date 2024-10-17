package com.oasisfeng.island.domain.usecases.passwordManager

import com.oasisfeng.island.domain.repositories.PasswordManager
import javax.inject.Inject

class SetPasswordUseCase @Inject constructor(private val repository: PasswordManager) {
  suspend operator fun invoke(password: CharArray) {
    repository.setPassword(password)
  }
}
