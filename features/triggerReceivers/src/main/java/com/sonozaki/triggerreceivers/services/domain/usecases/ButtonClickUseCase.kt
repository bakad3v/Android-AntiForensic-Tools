package com.sonozaki.triggerreceivers.services.domain.usecases

import android.util.Log
import com.sonozaki.entities.ButtonClicked
import com.sonozaki.entities.ButtonSelected
import com.sonozaki.entities.ButtonSettings
import com.sonozaki.entities.PowerButtonTriggerOptions
import com.sonozaki.entities.VolumeButtonTriggerOptions
import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 * Handle power button click
 */
class ButtonClickUseCase @Inject constructor(private val buttonSettingsRepository: ReceiversRepository) {

    private val mutex = Mutex()

    /**
     * Get latency specified for selected button
     */
    private fun getLatency(buttonSettings: ButtonSettings, buttonSelected: ButtonSelected): Int {
        return when(buttonSelected) {
            ButtonSelected.VOLUME_BUTTON -> buttonSettings.latencyVolumeButton
            ButtonSelected.POWER_BUTTON -> when(buttonSettings.triggerOnButton) {
                PowerButtonTriggerOptions.IGNORE -> -1 //must never occur
                PowerButtonTriggerOptions.DEPRECATED_WAY -> buttonSettings.latencyUsualMode
                PowerButtonTriggerOptions.SUPERUSER_WAY -> buttonSettings.latencyRootMode
            }
        }
    }

    /**
     * Get allowed clicks for selected button
     */
    private fun getAllowedClicks(buttonSettings: ButtonSettings, buttonSelected: ButtonSelected): Int {
        return when(buttonSelected) {
            ButtonSelected.POWER_BUTTON -> buttonSettings.allowedClicks
            ButtonSelected.VOLUME_BUTTON -> buttonSettings.volumeButtonAllowedClicks
        }
    }

    private suspend fun buttonClick(buttonSettings: ButtonSettings, buttonSelected: ButtonSelected): Boolean {
        val timestamp = System.currentTimeMillis()
        with(buttonSettingsRepository) {
            val buttonClicksData = getButtonClicksData(buttonSelected)
            Log.w("buttonClicks",buttonClicksData.toString())
            //if no clicks were performed previously, start counting clicks and return
            if (buttonClicksData.clicksInRow == 0) {
                setClicksInRow(1, buttonSelected)
                Log.w("buttonClicks2",buttonClicksData.toString())
                setLastTimestamp(timestamp, buttonSelected)
                return false
            }
            val latency = getLatency(buttonSettings, buttonSelected)
            if (timestamp - buttonClicksData.lastTimestamp <= latency) {
                setClicksInRow(buttonClicksData.clicksInRow + 1, buttonSelected)
                Log.w("buttonClicks3",buttonClicksData.toString())
                setLastTimestamp(timestamp, buttonSelected)
            } else { //if delay between last clicks and this click is smaller than latency, update number of clicks and return
                setClicksInRow(1, buttonSelected)
                Log.w("buttonClicks4",buttonClicksData.toString())
                setLastTimestamp(timestamp, buttonSelected)
                return false
            }  //else allow for deletion and start counting clicks again
            val allowedClicks = getAllowedClicks(buttonSettings, buttonSelected)
            if (buttonClicksData.clicksInRow + 1 == allowedClicks) {
                setClicksInRow(0, buttonSelected)
                Log.w("buttonClicks5",buttonClicksData.toString())
                return true
            }
            return false
        }
    }

    private fun isPowerButtonSelected(buttonSettings: ButtonSettings, buttonClicked: ButtonClicked): Boolean {
        return when (buttonSettings.triggerOnButton) {
            PowerButtonTriggerOptions.IGNORE -> false
            PowerButtonTriggerOptions.SUPERUSER_WAY, PowerButtonTriggerOptions.DEPRECATED_WAY -> {
                buttonClicked == ButtonClicked.POWER_BUTTON
            }
        }
    }

    private fun isVolumeButtonSelected(buttonSettings: ButtonSettings, buttonClicked: ButtonClicked): Boolean {
        return when (buttonSettings.triggerOnVolumeButton) {
            VolumeButtonTriggerOptions.IGNORE -> false
            VolumeButtonTriggerOptions.ON_VOLUME_DOWN -> buttonClicked == ButtonClicked.VOLUME_DOWN
            VolumeButtonTriggerOptions.ON_VOLUME_UP -> buttonClicked == ButtonClicked.VOLUME_UP
        }
    }

    private fun getButtonSelected(buttonSettings: ButtonSettings, buttonClicked: ButtonClicked): ButtonSelected? {
        if (isVolumeButtonSelected(buttonSettings, buttonClicked)) {
            return ButtonSelected.VOLUME_BUTTON
        }
        if (isPowerButtonSelected(buttonSettings, buttonClicked)) {
            return ButtonSelected.POWER_BUTTON
        }
        return null
    }

    suspend operator fun invoke(buttonClicked: ButtonClicked): Boolean {
        Log.w("buttonClicks",buttonClicked.toString())
        mutex.withLock {
            val buttonSettings = buttonSettingsRepository.getButtonSettings()
            Log.w("buttonClicks",buttonSettings.toString())
            val buttonSelected = getButtonSelected(buttonSettings, buttonClicked)
            Log.w("buttonClicks", buttonSelected.toString())
            //if no correct button was clicked return
            if (buttonSelected == null) {
                return false
            }
            val result = buttonClick(buttonSettings,buttonSelected)
            return result
        }
    }
}