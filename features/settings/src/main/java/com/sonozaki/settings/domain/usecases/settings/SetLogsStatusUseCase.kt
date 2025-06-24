package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.superuser.superuser.SuperUserManager
import javax.inject.Inject

class SetLogsStatusUseCase @Inject constructor(private val superUserManager: SuperUserManager) {
    suspend operator fun invoke(enable: Boolean) {
        superUserManager.getSuperUser().changeLogsStatus(enable)
    }
}