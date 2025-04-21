package com.sonozaki.services.domain.usecases

import com.sonozaki.services.domain.repository.ServicesRepository
import javax.inject.Inject

class GetRebootEnabledUseCase @Inject constructor(private val servicesRepository: ServicesRepository) {
    suspend operator fun invoke(): Boolean {
        return servicesRepository.getRebootEnabled()
    }
}