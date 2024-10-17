package com.oasisfeng.island.domain.usecases.bruteforce

import com.oasisfeng.island.domain.repositories.BruteforceRepository
import javax.inject.Inject

class OnWrongPasswordUseCase @Inject constructor(private val repository: BruteforceRepository) {
    suspend operator fun invoke(): Boolean {
        return repository.onWrongPassword()
    }
}