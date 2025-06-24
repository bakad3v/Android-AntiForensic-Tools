package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.superuser.superuser.SuperUserManager
import javax.inject.Inject

class SetSafeBootRestrictionUseCase @Inject constructor(private val superUserManager: SuperUserManager) {
    suspend operator fun invoke(status: Boolean) {
        superUserManager.getSuperUser().setSafeBootStatus(status)
    }
}