package net.typeblog.shelter.domain.usecases.apps

import net.typeblog.shelter.domain.entities.AppDomain
import net.typeblog.shelter.domain.repositories.AppsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetManagedAppsUseCase @Inject constructor(private val appsRepository: AppsRepository){
    operator fun invoke(): Flow<List<AppDomain>> {
        return appsRepository.getManagedApps()
    }
}