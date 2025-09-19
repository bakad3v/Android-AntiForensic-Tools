package com.bakasoft.appupdatecenter.domain.usecases

import com.bakasoft.appupdatecenter.domain.repository.AppUpdateCenterRepository
import com.sonozaki.entities.Permissions
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPermissionsUseCase @Inject constructor(
    private val repository: AppUpdateCenterRepository) {
    operator fun invoke(): Flow<Permissions> {
        return repository.permissions
    }
}