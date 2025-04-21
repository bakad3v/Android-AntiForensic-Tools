package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class WriteLogsUseCase @Inject constructor(
    private val receiversRepository: ReceiversRepository
) {
    suspend operator fun invoke(text: String) {
        receiversRepository.writeToLogs(text)
    }
}