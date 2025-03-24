package com.sonozaki.superuser.domain.usecases

import com.sonozaki.superuser.domain.repository.SuperUserRepository
import javax.inject.Inject

class SetOwnerInactiveUseCase @Inject constructor(private val repository: SuperUserRepository) {
    suspend operator fun invoke() {
        return repository.setOwnerInactive()
    }
}