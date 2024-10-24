package com.android.aftools.domain.usecases.bruteforce

import com.android.aftools.domain.repositories.BruteforceRepository
import javax.inject.Inject

class SetBruteForceLimitUseCase @Inject constructor(private val repository: BruteforceRepository) {
    suspend operator fun invoke(limit: Int) {
        repository.setBruteforceLimit(limit)
    }
}