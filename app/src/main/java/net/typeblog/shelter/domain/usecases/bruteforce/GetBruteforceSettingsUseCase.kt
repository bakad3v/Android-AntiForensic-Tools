package net.typeblog.shelter.domain.usecases.bruteforce

import net.typeblog.shelter.domain.entities.BruteforceSettings
import net.typeblog.shelter.domain.repositories.BruteforceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBruteforceSettingsUseCase @Inject constructor(private val repository: BruteforceRepository) {
    operator fun invoke(): Flow<BruteforceSettings> {
        return repository.bruteforceSettings
    }
}