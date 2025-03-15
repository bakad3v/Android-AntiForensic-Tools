package com.android.aftools.domain.usecases.button

import com.android.aftools.domain.repositories.ButtonSettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Handle power button click
 */
class ButtonClickUseCase @Inject constructor(private val buttonSettingsRepository: ButtonSettingsRepository) {
    suspend operator fun invoke(): Boolean {
        val timestamp = System.currentTimeMillis()
        with(buttonSettingsRepository) {
            val buttonSettings = buttonSettings.first()
            if (!buttonSettings.triggerOnButton) {
                return false
            } //continue if data destruction can be activated by power button clicks
            val buttonClicksData = getButtonClicksData()
            if (buttonClicksData.clicksInRow == 0) {
                setClicksInRow(1)
                setLastTimestamp(timestamp)
                return false
            }
            if (timestamp - buttonClicksData.lastTimestamp <= buttonSettings.latency) {
                setClicksInRow(buttonClicksData.clicksInRow+1)
                setLastTimestamp(timestamp)
            } else { //if delay between last clicks and this click is smaller than latency, update number of clicks
                setClicksInRow(1)
                setLastTimestamp(timestamp)
                return false
            }  //else start counting clicks again
            if (buttonClicksData.clicksInRow+1 == buttonSettings.allowedClicks) {
                setClicksInRow(0)
                return true
            }
            return false
        }
    }
}