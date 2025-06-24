package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.superuser.superuser.SuperUserManager
import javax.inject.Inject

class GetUserLimitUseCase @Inject constructor(private val superUserManager: SuperUserManager) {
    suspend operator fun invoke(): Int? {
        return superUserManager.getSuperUser().getUserLimit()
    }
}