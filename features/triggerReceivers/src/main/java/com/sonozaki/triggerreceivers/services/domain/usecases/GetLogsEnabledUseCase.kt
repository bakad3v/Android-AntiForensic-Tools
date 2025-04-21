package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class GetLogsEnabledUseCase @Inject constructor(
    private val receiversRepository: ReceiversRepository
) {
    suspend operator fun invoke(): Boolean {
        return receiversRepository.areLogsEnabled()
    }
}