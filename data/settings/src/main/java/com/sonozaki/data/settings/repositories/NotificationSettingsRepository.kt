package com.sonozaki.data.settings.repositories

import com.sonozaki.entities.NotificationSettings
import kotlinx.coroutines.flow.Flow

interface NotificationSettingsRepository {
    val notificationSettings: Flow<NotificationSettings>
    suspend fun setNotificationSettings(notificationSettings: NotificationSettings)
}