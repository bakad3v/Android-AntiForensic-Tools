package com.sonozaki.settings.domain.usecases.notifications

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class GetNotificationSettingsUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
    operator fun invoke() = repository.notificationSettings
}