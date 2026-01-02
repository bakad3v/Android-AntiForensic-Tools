package com.sonozaki.data.settings.repositories

import android.content.Context
import androidx.datastore.deviceProtectedDataStore
import com.sonozaki.data.settings.dataMigration.ButtonSettingsMigrationV1
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.ButtonClicksData
import com.sonozaki.entities.ButtonSelected
import com.sonozaki.entities.ButtonSettings
import com.sonozaki.entities.PowerButtonTriggerOptions
import com.sonozaki.entities.VolumeButtonTriggerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ButtonSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    buttonSettingsSerializer: EncryptedSerializer<ButtonSettings>,
    buttonSettingsMigrationV1: ButtonSettingsMigrationV1) :
    ButtonSettingsRepository {
    private var powerButtonClicksData = ButtonClicksData()
    private var volumeButtonClicksData = ButtonClicksData()

    private val Context.buttonDataStore by deviceProtectedDataStore(
        DATASTORE_NAME,
        buttonSettingsSerializer,
        produceMigrations = {
            listOf(buttonSettingsMigrationV1)
        }
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

    override suspend fun updateVolumeLatency(latency: Int) {
        context.buttonDataStore.updateData {
            it.copy(latencyVolumeButton = latency)
        }
    }

    override suspend fun updateAllowedVolumeButtonClicks(allowedClicks: Int) {
        context.buttonDataStore.updateData {
            it.copy(volumeButtonAllowedClicks = allowedClicks)
        }
    }

    override suspend fun updateAllowedClicks(allowedClicks: Int) {
        context.buttonDataStore.updateData {
            it.copy(allowedClicks = allowedClicks)
        }
    }

    override fun getButtonClicksData(buttonSelected: ButtonSelected): ButtonClicksData {
        return when(buttonSelected) {
            ButtonSelected.POWER_BUTTON -> powerButtonClicksData
            ButtonSelected.VOLUME_BUTTON -> volumeButtonClicksData
        }
    }

    override fun setClicksInRow(clicks: Int, buttonSelected: ButtonSelected) {
        when(buttonSelected) {
            ButtonSelected.POWER_BUTTON -> powerButtonClicksData = powerButtonClicksData.copy(clicksInRow = clicks)
            ButtonSelected.VOLUME_BUTTON -> volumeButtonClicksData = volumeButtonClicksData.copy(clicksInRow = clicks)
        }
    }

    override fun setLastTimestamp(timestamp: Long, buttonSelected: ButtonSelected) {
        when(buttonSelected) {
            ButtonSelected.POWER_BUTTON -> powerButtonClicksData = powerButtonClicksData.copy(lastTimestamp = timestamp)
            ButtonSelected.VOLUME_BUTTON -> volumeButtonClicksData =  volumeButtonClicksData.copy(lastTimestamp = timestamp)
        }
    }

    override suspend fun setTriggerOnButtonStatus(status: PowerButtonTriggerOptions) {
        context.buttonDataStore.updateData {
            it.copy(triggerOnButton = status)
        }
    }

    override suspend fun setTriggerOnVolumeButtonStatus(status: VolumeButtonTriggerOptions) {
        context.buttonDataStore.updateData {
            it.copy(triggerOnVolumeButton = status)
        }
    }

    companion object {
        private const val DATASTORE_NAME = "button_datastore_v2.json"
    }
}