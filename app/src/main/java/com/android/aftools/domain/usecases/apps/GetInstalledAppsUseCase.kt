package com.android.aftools.domain.usecases.apps

import com.android.aftools.domain.entities.AppDomain
import com.android.aftools.domain.repositories.AppsRepository
import javax.inject.Inject

class GetInstalledAppsUseCase @Inject constructor(private val appsRepository: AppsRepository){
    operator fun invoke(): List<AppDomain> {
        return appsRepository.getInstalledApplications()
    }
}