package com.bakasoft.appupdatecenter.domain.usecases

import com.bakasoft.appupdatecenter.domain.repository.AppUpdateCenterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPopupStatusUseCase @Inject constructor(
    private val repository: AppUpdateCenterRepository) {
    operator fun invoke(): Flow<Boolean> {
        return repository.showUpdatePopupStatus
    }
}