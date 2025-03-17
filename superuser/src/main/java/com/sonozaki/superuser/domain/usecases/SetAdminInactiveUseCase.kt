package com.sonozaki.superuser.domain.usecases

import com.sonozaki.superuser.domain.repository.SuperUserRepository
import javax.inject.Inject

class SetAdminInactiveUseCase @Inject constructor(private val repository: SuperUserRepository) {
    operator fun invoke() {
        return repository.setAdminInactive()
    }
}