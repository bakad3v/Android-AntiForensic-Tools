package net.typeblog.shelter.domain.usecases.bruteforce

import net.typeblog.shelter.domain.repositories.BruteforceRepository
import javax.inject.Inject

class SetBruteForceLimitUseCase @Inject constructor(private val repository: BruteforceRepository) {
    suspend operator fun invoke(limit: Int) {
        repository.setBruteforceLimit(limit)
    }
}