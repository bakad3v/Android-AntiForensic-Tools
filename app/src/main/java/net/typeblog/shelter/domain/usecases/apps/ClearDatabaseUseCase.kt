package net.typeblog.shelter.domain.usecases.apps

import net.typeblog.shelter.domain.repositories.AppsRepository
import javax.inject.Inject

class ClearDatabaseUseCase @Inject constructor(private val appsRepository: AppsRepository){
    suspend operator fun invoke() {
        return appsRepository.clearDb()
    }
}