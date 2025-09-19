package com.bakasoft.appupdatecenter.domain.usecases

import com.bakasoft.appupdatecenter.domain.repository.AppUpdateCenterRepository
import com.bakasoft.network.RequestResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppUpdateDataFlowUseCase @Inject constructor(
    private val repository: AppUpdateCenterRepository) {
    operator fun invoke(): Flow<RequestResult<com.sonozaki.entities.AppLatestVersion>> {
        return repository.appUpdateDataFlow
    }
}