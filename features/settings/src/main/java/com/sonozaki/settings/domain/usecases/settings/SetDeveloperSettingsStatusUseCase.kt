package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.superuser.superuser.SuperUserManager
import javax.inject.Inject

class SetDeveloperSettingsStatusUseCase @Inject constructor(private val superUserManager: SuperUserManager) {
    suspend operator fun invoke(status: Boolean) {
        superUserManager.getSuperUser().changeDeveloperSettingsStatus(status)
    }
}