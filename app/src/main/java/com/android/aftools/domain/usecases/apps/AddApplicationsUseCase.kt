package com.android.aftools.domain.usecases.apps

import com.android.aftools.domain.entities.AppDomain
import com.android.aftools.domain.repositories.AppsRepository
import javax.inject.Inject

class AddApplicationsUseCase @Inject constructor(private val appsRepository: AppsRepository){
    suspend operator fun invoke(apps: List<AppDomain>) {
        return appsRepository.addApplications(apps)
    }
}