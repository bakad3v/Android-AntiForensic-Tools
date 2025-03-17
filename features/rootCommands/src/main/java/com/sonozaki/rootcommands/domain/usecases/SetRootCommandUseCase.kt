package com.sonozaki.rootcommands.domain.usecases

import com.sonozaki.rootcommands.domain.repository.RootScreenRepository
import javax.inject.Inject

class SetRootCommandUseCase @Inject constructor(private val repository: RootScreenRepository) {
    suspend operator fun invoke(command: String) {
        return repository.setRootCommands(command)
    }
}