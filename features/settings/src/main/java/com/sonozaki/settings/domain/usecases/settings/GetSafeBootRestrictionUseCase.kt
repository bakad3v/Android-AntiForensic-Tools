package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.superuser.superuser.SuperUserManager
import javax.inject.Inject

class GetSafeBootRestrictionUseCase @Inject constructor(private val superUserManager: SuperUserManager) {
    suspend operator fun invoke(): Boolean {
        return superUserManager.getSuperUser().getSafeBootStatus()
    }
}