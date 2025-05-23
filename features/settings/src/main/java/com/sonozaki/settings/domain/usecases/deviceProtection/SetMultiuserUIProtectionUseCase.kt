package com.sonozaki.settings.domain.usecases.deviceProtection

import com.sonozaki.entities.MultiuserUIProtection
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetMultiuserUIProtectionUseCase @Inject constructor(
    private val settingsScreenRepository: SettingsScreenRepository
) {
    suspend operator fun invoke(multiuserUIProtection: MultiuserUIProtection) {
        settingsScreenRepository.changeMultiuserUIProtection(multiuserUIProtection)
    }
}