package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.entities.NotificationSettings
import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class SetNotificationSettingsUseCase @Inject constructor(private val repository: ReceiversRepository) {
    suspend operator fun invoke(settings: NotificationSettings) {
        repository.setNotificationSettings(settings)
    }
}