package com.android.aftools.domain.usecases.bruteforce

import com.android.aftools.domain.entities.BruteforceSettings
import com.android.aftools.domain.repositories.BruteforceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBruteforceSettingsUseCase @Inject constructor(private val repository: BruteforceRepository) {
    operator fun invoke(): Flow<BruteforceSettings> {
        return repository.bruteforceSettings
    }
}