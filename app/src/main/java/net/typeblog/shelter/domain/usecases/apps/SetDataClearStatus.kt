package net.typeblog.shelter.domain.usecases.apps

import net.typeblog.shelter.domain.repositories.AppsRepository
import javax.inject.Inject

class SetDataClearStatus @Inject constructor(private val appsRepository: AppsRepository){
    suspend operator fun invoke(status: Boolean,packageName: String) {
        return appsRepository.setDataClearStatus(status, packageName)
    }
}