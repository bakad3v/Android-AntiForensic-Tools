package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class SetAdminActiveUseCase @Inject constructor(private val repository: ReceiversRepository) {
    suspend operator fun invoke(active: Boolean) {
        repository.setAdminActive(active)
    }
}