package com.sonozaki.settings.domain.usecases.bruteforce

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetBruteForceLimitUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
    suspend operator fun invoke(limit: Int) {
        repository.setBruteforceLimit(limit)
    }
}