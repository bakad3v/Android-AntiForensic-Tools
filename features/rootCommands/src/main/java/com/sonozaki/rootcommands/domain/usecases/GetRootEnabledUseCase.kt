package com.sonozaki.rootcommands.domain.usecases

import com.sonozaki.rootcommands.domain.repository.RootScreenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRootEnabledUseCase @Inject constructor(private val repository: RootScreenRepository) {
    operator fun invoke(): Flow<Boolean> {
        return repository.runRootEnabled
    }
}