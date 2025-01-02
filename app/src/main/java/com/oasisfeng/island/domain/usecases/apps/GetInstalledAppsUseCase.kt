package com.oasisfeng.island.domain.usecases.apps

import com.oasisfeng.island.domain.entities.AppDomain
import com.oasisfeng.island.domain.repositories.AppsRepository
import javax.inject.Inject

class GetInstalledAppsUseCase @Inject constructor(private val appsRepository: AppsRepository){
    operator fun invoke(): List<AppDomain> {
        return appsRepository.getInstalledApplications()
    }
}