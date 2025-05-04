package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.entities.Permissions
import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class GetPermissionsUseCase @Inject constructor(val receiversRepository: ReceiversRepository) {
    suspend operator fun invoke(): Permissions {
        return receiversRepository.getPermissions()
    }
}