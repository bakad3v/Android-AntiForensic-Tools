package com.sonozaki.rootcommands.domain.usecases

import com.sonozaki.rootcommands.domain.repository.RootScreenRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetRootCommandUseCase @Inject constructor(private val repository: RootScreenRepository) {
    suspend operator fun invoke(): String? {
        if (!repository.getRootPermission()) {
            return null
        }
        return repository.getRootCommands()
    }
}