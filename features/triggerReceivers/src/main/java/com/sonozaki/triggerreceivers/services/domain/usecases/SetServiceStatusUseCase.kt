package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class SetServiceStatusUseCase @Inject constructor(private val repository: ReceiversRepository) {
  suspend operator fun invoke(working: Boolean) {
    repository.setServiceStatus(working)
  }
}
