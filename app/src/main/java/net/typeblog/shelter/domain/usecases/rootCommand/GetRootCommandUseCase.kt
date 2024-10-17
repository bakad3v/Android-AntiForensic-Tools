package net.typeblog.shelter.domain.usecases.rootCommand

import net.typeblog.shelter.domain.repositories.PermissionsRepository
import net.typeblog.shelter.domain.repositories.RootRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetRootCommandUseCase @Inject constructor(private val repository: RootRepository, private val permissionsRepository: PermissionsRepository) {
    suspend operator fun invoke(): String? {
        if (!permissionsRepository.permissions.first().isRoot) {
            return null
        }
        return repository.getRootCommand().first()
    }
}