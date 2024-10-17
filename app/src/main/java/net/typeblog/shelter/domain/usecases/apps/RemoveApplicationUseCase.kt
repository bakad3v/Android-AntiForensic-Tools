package net.typeblog.shelter.domain.usecases.apps

import net.typeblog.shelter.domain.repositories.AppsRepository
import javax.inject.Inject

class RemoveApplicationUseCase @Inject constructor(private val appsRepository: AppsRepository){
    suspend operator fun invoke(packageName: String) {
        return appsRepository.removeApplication(packageName)
    }
}