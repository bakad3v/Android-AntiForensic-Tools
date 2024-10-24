package com.android.aftools.domain.usecases.permissions

import com.android.aftools.domain.repositories.PermissionsRepository
import javax.inject.Inject

class SetRootActiveUseCase @Inject constructor(private val repository: PermissionsRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setRootStatus(status)
    }
}
