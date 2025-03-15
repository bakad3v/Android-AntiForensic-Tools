package com.android.aftools.data.repositories

import android.content.Context
import com.android.aftools.data.encryption.EncryptedSerializer
import com.android.aftools.datastoreDBA.dataStoreDirectBootAware
import com.android.aftools.domain.entities.ButtonClicksData
import com.android.aftools.domain.entities.ButtonSettings
import com.android.aftools.domain.repositories.ButtonSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ButtonSettingsRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context, buttonSettingsSerializer: EncryptedSerializer<ButtonSettings>) : ButtonSettingsRepository {
    private var buttonClicksData = ButtonClicksData()
    private val Context.buttonDataStore by dataStoreDirectBootAware(DATASTORE_NAME, buttonSettingsSerializer)

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