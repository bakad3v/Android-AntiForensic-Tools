package net.typeblog.shelter.domain.usecases.button

import android.util.Log
import net.typeblog.shelter.domain.repositories.ButtonSettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ButtonClickUseCase @Inject constructor(private val buttonSettingsRepository: ButtonSettingsRepository) {
    suspend operator fun invoke(): Boolean {
        val timestamp = System.currentTimeMillis()
        with(buttonSettingsRepository) {
            val buttonSettings = buttonSettings.first()
            if (!buttonSettings.triggerOnButton) {
                return false
            }
            val buttonClicksData = getButtonClicksData()
            if (buttonClicksData.clicksInRow == 0) {
                setClicksInRow(1)
                setLastTimestamp(timestamp)
                return false
            }
            if (timestamp - buttonClicksData.lastTimestamp <= buttonSettings.latency) {
                setClicksInRow(buttonClicksData.clicksInRow+1)
                setLastTimestamp(timestamp)
            } else {
                setClicksInRow(1)
                setLastTimestamp(timestamp)
                return false
            }
            if (buttonClicksData.clicksInRow+1 == buttonSettings.allowedClicks) {
                setClicksInRow(0)
                return true
            }
            return false
        }
    }
}