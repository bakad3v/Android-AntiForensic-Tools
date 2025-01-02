package net.typeblog.shelter.domain.usecases.permissions

import net.typeblog.shelter.domain.repositories.PermissionsRepository
import javax.inject.Inject

class SetAdminActiveUseCase @Inject constructor(private val repository: PermissionsRepository) {
    suspend operator fun invoke(active: Boolean) {
        repository.setAdminStatus(active)
    }
}