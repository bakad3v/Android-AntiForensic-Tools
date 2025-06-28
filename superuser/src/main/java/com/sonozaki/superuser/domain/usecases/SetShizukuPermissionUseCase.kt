package com.sonozaki.superuser.domain.usecases

import com.sonozaki.superuser.domain.repository.SuperUserRepository
import javax.inject.Inject

class SetShizukuPermissionUseCase @Inject constructor(private val repository: SuperUserRepository) {
    suspend operator fun invoke(active: Boolean) {
        return repository.setShizukuPermission(active)
    }
}