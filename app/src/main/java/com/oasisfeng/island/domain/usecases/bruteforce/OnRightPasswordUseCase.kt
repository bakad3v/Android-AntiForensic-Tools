package com.oasisfeng.island.domain.usecases.bruteforce

import com.oasisfeng.island.domain.repositories.BruteforceRepository
import javax.inject.Inject

class OnRightPasswordUseCase @Inject constructor(private val repository: BruteforceRepository) {
    suspend operator fun invoke() {
        repository.onRightPassword()
    }
}