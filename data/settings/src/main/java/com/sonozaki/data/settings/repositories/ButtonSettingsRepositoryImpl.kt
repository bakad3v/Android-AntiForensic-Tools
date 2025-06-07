package com.sonozaki.data.settings.repositories

import android.content.Context
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.data.settings.dataMigration.ButtonSettingsMigrationV1
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.entities.ButtonClicksData
import com.sonozaki.entities.ButtonSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ButtonSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    buttonSettingsSerializer: BaseSerializer<ButtonSettings>,
    buttonSettingsMigrationV1: ButtonSettingsMigrationV1) :
    ButtonSettingsRepository {
    private var buttonClicksData = ButtonClicksData()
    private val Context.buttonDataStore by encryptedDataStore(
        DATASTORE_NAME,
        buttonSettingsSerializer,
        produceMigrations = { context ->
            listOf<ButtonSettingsMigrationV1>(buttonSettingsMigrationV1)
        },
        alias = EncryptionAlias.DATASTORE.name,
        isDBA = true
    )

    override val buttonSettings = context.buttonDataStore.data


    override suspend fun updateLatency(latency: Int) {
        context.buttonDataStore.updateData {
            it.copy(latencyUsualMode=latency)
        }
    }

    override suspend fun updateRootLatency(latency: Int) {
        context.buttonDataStore.updateData {
            it.copy(latencyRootMode = latency)
        }
    }

    override suspend fun updateAllowedClicks(allowedClicks: Int) {
        context.buttonDataStore.updateData {
            it.copy(allowedClicks = allowedClicks)
        }
    }

    override fun getButtonClicksData(): ButtonClicksData {
        return buttonClicksData
    }

    override fun setClicksInRow(clicks: Int) {
        buttonClicksData = buttonClicksData.copy(clicksInRow = clicks)
    }

    override fun setLastTimestamp(timestamp: Long) {
        buttonClicksData= buttonClicksData.copy(lastTimestamp = timestamp)
    }

    override suspend fun setTriggerOnButtonStatus(status: Boolean) {
        context.buttonDataStore.updateData {
            it.copy(triggerOnButton = status)
        }
    }

    companion object {
        private const val DATASTORE_NAME = "button_datastore_v2.json"
    }
}