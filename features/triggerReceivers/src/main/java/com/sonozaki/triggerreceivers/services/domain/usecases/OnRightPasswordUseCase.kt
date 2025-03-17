package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class OnRightPasswordUseCase @Inject constructor(private val repository: ReceiversRepository) {
    suspend operator fun invoke() {
        repository.onRightPassword()
    }
}