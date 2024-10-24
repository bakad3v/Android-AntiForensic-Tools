package com.android.aftools.domain.usecases.rootCommand

import com.android.aftools.domain.repositories.RootRepository
import javax.inject.Inject

class SetRootCommandUseCase @Inject constructor(private val repository: RootRepository) {
    suspend operator fun invoke(command: String) {
        return repository.setRootCommand(command)
    }
}