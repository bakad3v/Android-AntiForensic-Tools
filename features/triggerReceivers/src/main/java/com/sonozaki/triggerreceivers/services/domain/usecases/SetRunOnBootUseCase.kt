package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class SetRunOnBootUseCase @Inject constructor(private val repository: ReceiversRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setRunOnBoot(status)
    }
}
