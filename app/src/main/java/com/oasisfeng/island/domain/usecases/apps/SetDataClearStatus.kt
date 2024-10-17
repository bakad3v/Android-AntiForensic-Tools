package com.oasisfeng.island.domain.usecases.apps

import com.oasisfeng.island.domain.repositories.AppsRepository
import javax.inject.Inject

class SetDataClearStatus @Inject constructor(private val appsRepository: AppsRepository){
    suspend operator fun invoke(status: Boolean,packageName: String) {
        return appsRepository.setDataClearStatus(status, packageName)
    }
}