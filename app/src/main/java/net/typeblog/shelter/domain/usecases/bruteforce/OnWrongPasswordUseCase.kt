package net.typeblog.shelter.domain.usecases.bruteforce

import net.typeblog.shelter.domain.repositories.BruteforceRepository
import javax.inject.Inject

class OnWrongPasswordUseCase @Inject constructor(private val repository: BruteforceRepository) {
    suspend operator fun invoke(): Boolean {
        return repository.onWrongPassword()
    }
}