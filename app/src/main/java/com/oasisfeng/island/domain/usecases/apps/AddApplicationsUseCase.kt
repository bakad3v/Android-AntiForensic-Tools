package com.oasisfeng.island.domain.usecases.apps

import com.oasisfeng.island.domain.entities.AppDomain
import com.oasisfeng.island.domain.repositories.AppsRepository
import javax.inject.Inject

class AddApplicationsUseCase @Inject constructor(private val appsRepository: AppsRepository){
    suspend operator fun invoke(apps: List<AppDomain>) {
        return appsRepository.addApplications(apps)
    }
}