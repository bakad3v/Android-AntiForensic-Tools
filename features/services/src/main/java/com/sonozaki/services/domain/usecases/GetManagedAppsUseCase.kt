package com.sonozaki.services.domain.usecases

import com.sonozaki.entities.App
import com.sonozaki.services.domain.repository.ServicesRepository
import javax.inject.Inject

class GetManagedAppsUseCase @Inject constructor(private val repository: ServicesRepository) {
    operator fun invoke(): List<App> {
        return repository.getManagedApps()
    }
}