package com.bakasoft.appupdatecenter.domain.usecases

import com.bakasoft.appupdatecenter.domain.repository.AppUpdateCenterRepository
import javax.inject.Inject

class CheckUpdatesUseCase @Inject constructor(
    private val repository: AppUpdateCenterRepository) {
    suspend operator fun invoke() {
        return repository.checkUpdates()
    }
}