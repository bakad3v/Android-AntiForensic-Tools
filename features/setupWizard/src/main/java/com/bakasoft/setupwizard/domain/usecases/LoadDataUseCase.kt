package com.bakasoft.setupwizard.domain.usecases

import com.bakasoft.setupwizard.domain.repository.SetupWizardRepository
import javax.inject.Inject

class LoadDataUseCase @Inject constructor(private val repository: SetupWizardRepository) {
    suspend operator fun invoke() {
        repository.checkUpdates()
      //  repository.updateWizard()
        repository.refreshProfiles()
    }
}