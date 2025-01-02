package net.typeblog.shelter.domain.usecases.apps

import net.typeblog.shelter.domain.entities.AppDomain
import net.typeblog.shelter.domain.repositories.AppsRepository
import javax.inject.Inject

class GetInstalledAppsUseCase @Inject constructor(private val appsRepository: AppsRepository){
    operator fun invoke(): List<AppDomain> {
        return appsRepository.getInstalledApplications()
    }
}