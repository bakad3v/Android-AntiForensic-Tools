package com.android.aftools.domain.usecases.apps

import com.android.aftools.domain.repositories.AppsRepository
import javax.inject.Inject

class ClearDatabaseUseCase @Inject constructor(private val appsRepository: AppsRepository){
    suspend operator fun invoke() {
        return appsRepository.clearDb()
    }
}