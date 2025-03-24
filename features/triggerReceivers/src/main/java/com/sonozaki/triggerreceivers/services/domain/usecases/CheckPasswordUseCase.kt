package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class CheckPasswordUseCase @Inject constructor(private val repository: ReceiversRepository) {
  suspend operator fun invoke(password: CharArray): Boolean {
    return repository.checkPassword(password)
  }
}
