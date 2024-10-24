package com.android.aftools.domain.usecases.bruteforce

import com.android.aftools.domain.repositories.BruteforceRepository
import javax.inject.Inject

class SetBruteForceStatusUseCase @Inject constructor(private val repository: BruteforceRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setBruteforceStatus(status)
    }
}