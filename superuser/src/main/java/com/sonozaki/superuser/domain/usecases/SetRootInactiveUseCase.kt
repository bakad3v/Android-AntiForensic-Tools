package com.sonozaki.superuser.domain.usecases

import com.sonozaki.entities.Permissions
import com.sonozaki.superuser.domain.repository.SuperUserRepository
import javax.inject.Inject

class SetRootInactiveUseCase @Inject constructor(private val repository: SuperUserRepository) {
    operator fun invoke() {
        return repository.setRootInactive()
    }
}