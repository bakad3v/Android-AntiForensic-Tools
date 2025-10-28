package com.sonozaki.data.settings.repositories

import android.content.Context
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.entities.NotificationSettings
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    notificationSettingsSerializer: BaseSerializer<NotificationSettings>
): NotificationSettingsRepository {

    private val Context.deviceProtectionSettingsDatastore by encryptedDataStore(
        DATASTORE_NAME,
        notificationSettingsSerializer,
        alias = EncryptionAlias.DATASTORE.name,
        isDBA = true
    )

    override val notificationSettings: Flow<NotificationSettings> = context.deviceProtectionSettingsDatastore.data

    override suspend fun setNotificationSettings(notificationSettings: NotificationSettings) {
        context.deviceProtectionSettingsDatastore.updateData { notificationSettings }
    }

    companion object {
        private const val DATASTORE_NAME = "notification_settings.json"
    }
}