package com.sonozaki.passwordsetup.domain.usecases

import com.sonozaki.passwordsetup.domain.repository.PasswordSetupRepository
import javax.inject.Inject

class SetPasswordUseCase @Inject constructor(private val repository: PasswordSetupRepository) {
  suspend operator fun invoke(password: CharArray) {
    repository.setPassword(password)
  }
}
