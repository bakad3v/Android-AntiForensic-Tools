package com.oasisfeng.island.domain.usecases.apps

import com.oasisfeng.island.domain.entities.AppDomain
import com.oasisfeng.island.domain.repositories.AppsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetManagedAppsUseCase @Inject constructor(private val appsRepository: AppsRepository){
    operator fun invoke(): Flow<List<AppDomain>> {
        return appsRepository.getManagedApps()
    }
}