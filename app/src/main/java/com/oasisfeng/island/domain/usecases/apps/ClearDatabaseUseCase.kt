package com.oasisfeng.island.domain.usecases.apps

import com.oasisfeng.island.domain.repositories.AppsRepository
import javax.inject.Inject

class ClearDatabaseUseCase @Inject constructor(private val appsRepository: AppsRepository){
    suspend operator fun invoke() {
        return appsRepository.clearDb()
    }
}