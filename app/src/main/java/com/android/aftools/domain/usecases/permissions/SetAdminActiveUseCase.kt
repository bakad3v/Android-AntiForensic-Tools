package com.android.aftools.domain.usecases.permissions

import com.android.aftools.domain.repositories.PermissionsRepository
import javax.inject.Inject

class SetAdminActiveUseCase @Inject constructor(private val repository: PermissionsRepository) {
    suspend operator fun invoke(active: Boolean) {
        repository.setAdminStatus(active)
    }
}