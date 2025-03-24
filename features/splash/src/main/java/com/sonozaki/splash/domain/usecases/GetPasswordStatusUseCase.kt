package com.sonozaki.splash.domain.usecases

import com.sonozaki.splash.domain.repository.SplashRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPasswordStatusUseCase @Inject constructor(private val repository: SplashRepository) {
    operator fun invoke(): Flow<Boolean> {
        return repository.getPasswordStatus()
    }
}