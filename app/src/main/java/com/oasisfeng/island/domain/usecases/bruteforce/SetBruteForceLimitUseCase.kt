package com.oasisfeng.island.domain.usecases.bruteforce

import com.oasisfeng.island.domain.repositories.BruteforceRepository
import javax.inject.Inject

class SetBruteForceLimitUseCase @Inject constructor(private val repository: BruteforceRepository) {
    suspend operator fun invoke(limit: Int) {
        repository.setBruteforceLimit(limit)
    }
}