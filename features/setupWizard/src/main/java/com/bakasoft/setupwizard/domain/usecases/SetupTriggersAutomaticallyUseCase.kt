package com.bakasoft.setupwizard.domain.usecases

import com.bakasoft.setupwizard.domain.entities.AvailableTriggers
import com.bakasoft.setupwizard.domain.repository.SetupWizardRepository
import javax.inject.Inject

class SetupTriggersAutomaticallyUseCase @Inject constructor(private val repository: SetupWizardRepository){
    suspend operator fun invoke(availableTriggers: AvailableTriggers) {
        when (availableTriggers) {
            is AvailableTriggers.NoTriggers -> {}
            is AvailableTriggers.VolumeButton -> {
                repository.setTriggerOnUsb()
                repository.setTriggerOnDuressPassword()
                repository.setTriggerOnVolumeButtonClicks()
                repository.setTriggerOnBruteforce()
            }
            is AvailableTriggers.PowerButton -> {
                repository.setTriggerOnUsb()
                repository.setTriggerOnDuressPassword()
                repository.setTriggerOnPowerButtonClicks()
                repository.setTriggerOnBruteforce()
            }
        }
    }
}