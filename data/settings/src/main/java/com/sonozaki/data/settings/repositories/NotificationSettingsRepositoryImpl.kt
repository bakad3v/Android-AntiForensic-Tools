package com.sonozaki.data.settings.repositories

import android.content.Context
import androidx.datastore.deviceProtectedDataStore
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.NotificationSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    notificationSettingsSerializer: EncryptedSerializer<NotificationSettings>
): NotificationSettingsRepository {

    private val Context.deviceProtectionSettingsDatastore by deviceProtectedDataStore(
        DATASTORE_NAME,
        notificationSettingsSerializer
    )

    override val notificationSettings: Flow<NotificationSettings> = context.deviceProtectionSettingsDatastore.data

    override suspend fun setNotificationSettings(notificationSettings: NotificationSettings) {
        context.deviceProtectionSettingsDatastore.updateData { notificationSettings }
    }

    companion object {
        private const val DATASTORE_NAME = "notification_settings.json"
    }
}