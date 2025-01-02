package com.oasisfeng.island.domain.usecases.apps

import com.oasisfeng.island.domain.repositories.AppsRepository
import javax.inject.Inject

class RemoveApplicationUseCase @Inject constructor(private val appsRepository: AppsRepository){
    suspend operator fun invoke(packageName: String) {
        return appsRepository.removeApplication(packageName)
    }
}