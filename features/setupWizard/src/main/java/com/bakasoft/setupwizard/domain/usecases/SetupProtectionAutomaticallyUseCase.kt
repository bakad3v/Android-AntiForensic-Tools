package com.bakasoft.setupwizard.domain.usecases

import com.bakasoft.setupwizard.domain.entities.AvailableProtection
import com.bakasoft.setupwizard.domain.repository.SetupWizardRepository
import com.sonozaki.superuser.superuser.SuperUserException
import javax.inject.Inject

class SetupProtectionAutomaticallyUseCase @Inject constructor(private val repository: SetupWizardRepository) {
    suspend operator fun invoke(availableProtection: AvailableProtection) {
        if (availableProtection.uninstallItself) {
            repository.setSelfDestruction()
        }
        if (availableProtection.activateTrim) {
            repository.activateTrim()
        }
        if (availableProtection.disableLogs) {
            try {
                repository.disableLogs()
            } catch (e: SuperUserException) {

            }
        }
        if (availableProtection.disableSafeBoot) {
            try {
                repository.disableSafeBoot()
            } catch (e: SuperUserException) {
            }
        }
        if (availableProtection.hideItself) {
            repository.setAppHiding()
        }
        if (availableProtection.hideMultiuserUI) {
            repository.setupMultiuser()
        }
    }
}