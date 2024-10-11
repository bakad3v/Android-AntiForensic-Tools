package com.android.aftools.domain.usecases.bruteforce

import com.android.aftools.domain.repositories.BruteforceRepository
import javax.inject.Inject

class OnWrongPasswordUseCase @Inject constructor(private val repository: BruteforceRepository) {
    suspend operator fun invoke(): Boolean {
        return repository.onWrongPassword()
    }
}