package com.oasisfeng.island.domain.usecases.bruteforce

import com.oasisfeng.island.domain.entities.BruteforceSettings
import com.oasisfeng.island.domain.repositories.BruteforceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBruteforceSettingsUseCase @Inject constructor(private val repository: BruteforceRepository) {
    operator fun invoke(): Flow<BruteforceSettings> {
        return repository.bruteforceSettings
    }
}