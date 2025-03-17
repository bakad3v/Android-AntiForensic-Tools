package com.sonozaki.data.settings.repositories

import android.content.Context
import com.sonozaki.entities.ButtonClicksData
import com.sonozaki.entities.ButtonSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ButtonSettingsRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context, buttonSettingsSerializer: com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer<ButtonSettings>) :
    ButtonSettingsRepository {
    private var buttonClicksData = ButtonClicksData()
    private val Context.buttonDataStore by com.sonozaki.encrypteddatastore.datastoreDBA.dataStoreDirectBootAware(
        DATASTORE_NAME,
        buttonSettingsSerializer
    )

    override val buttonSettings = context.buttonDataStore.data


    override suspend fun updateLatency(latency: Int) {
        context.buttonDataStore.updateData {
            it.copy(latency=latency)
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
        private const val DATASTORE_NAME = "button_datastore.json"
    }
}