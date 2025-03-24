package com.sonozaki.settings.domain.usecases.bruteforce

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetBruteForceStatusUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setBruteforceStatus(status)
    }
}