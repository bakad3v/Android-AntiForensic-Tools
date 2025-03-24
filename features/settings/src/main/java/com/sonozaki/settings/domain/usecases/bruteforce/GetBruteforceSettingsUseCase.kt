package com.sonozaki.settings.domain.usecases.bruteforce

import com.sonozaki.entities.BruteforceSettings
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBruteforceSettingsUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
    operator fun invoke(): Flow<BruteforceSettings> {
        return repository.bruteForceSettings
    }
}