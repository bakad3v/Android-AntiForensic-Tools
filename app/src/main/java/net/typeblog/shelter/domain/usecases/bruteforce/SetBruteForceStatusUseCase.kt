package net.typeblog.shelter.domain.usecases.bruteforce

import net.typeblog.shelter.domain.repositories.BruteforceRepository
import javax.inject.Inject

class SetBruteForceStatusUseCase @Inject constructor(private val repository: BruteforceRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setBruteforceStatus(status)
    }
}