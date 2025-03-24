package com.sonozaki.lockscreen.domain.usecases

import com.sonozaki.lockscreen.domain.repository.LockScreenRepository
import javax.inject.Inject

class CheckPasswordUseCase @Inject constructor(private val repository: LockScreenRepository) {
    suspend operator fun invoke(password: CharArray): Boolean {
        return repository.checkPassword(password)
    }
}