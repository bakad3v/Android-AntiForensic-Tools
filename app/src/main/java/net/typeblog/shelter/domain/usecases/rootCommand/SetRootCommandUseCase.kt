package net.typeblog.shelter.domain.usecases.rootCommand

import net.typeblog.shelter.domain.repositories.RootRepository
import javax.inject.Inject

class SetRootCommandUseCase @Inject constructor(private val repository: RootRepository) {
    suspend operator fun invoke(command: String) {
        return repository.setRootCommand(command)
    }
}