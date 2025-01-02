package net.typeblog.shelter.domain.usecases.apps

import net.typeblog.shelter.domain.entities.AppDomain
import net.typeblog.shelter.domain.repositories.AppsRepository
import javax.inject.Inject

class AddApplicationsUseCase @Inject constructor(private val appsRepository: AppsRepository){
    suspend operator fun invoke(apps: List<AppDomain>) {
        return appsRepository.addApplications(apps)
    }
}