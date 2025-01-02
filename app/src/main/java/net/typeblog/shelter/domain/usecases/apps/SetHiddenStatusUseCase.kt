package net.typeblog.shelter.domain.usecases.apps

import net.typeblog.shelter.domain.repositories.AppsRepository
import javax.inject.Inject

class SetHiddenStatusUseCase @Inject constructor(private val appsRepository: AppsRepository){
    suspend operator fun invoke(status: Boolean,packageName: String) {
        return appsRepository.setHiddenStatus(status, packageName)
    }
}