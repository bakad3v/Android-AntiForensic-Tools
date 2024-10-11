package com.android.aftools.domain.usecases.passwordManager

import com.android.aftools.domain.repositories.PasswordManager
import javax.inject.Inject

class SetPasswordUseCase @Inject constructor(private val repository: PasswordManager) {
  suspend operator fun invoke(password: CharArray) {
    repository.setPassword(password)
  }
}
