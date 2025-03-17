package com.sonozaki.rootcommands.domain.usecases

import com.sonozaki.rootcommands.domain.repository.RootScreenRepository
import javax.inject.Inject

class SetRunRootUseCase @Inject constructor(private val repository: RootScreenRepository) {
    suspend operator fun invoke(status: Boolean) {
        return repository.setRunRoot(status)
    }
}