package com.oasisfeng.island.domain.usecases.permissions

import com.oasisfeng.island.domain.repositories.PermissionsRepository
import javax.inject.Inject

class SetOwnerActiveUseCase @Inject constructor(private val repository: PermissionsRepository) {
    suspend operator fun invoke(active: Boolean) {
        repository.setOwnerStatus(active)
    }
}