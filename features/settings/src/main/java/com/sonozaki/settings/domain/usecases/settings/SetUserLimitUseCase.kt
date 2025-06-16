package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.superuser.superuser.SuperUserManager
import javax.inject.Inject

class SetUserLimitUseCase @Inject constructor(private val superUserManager: SuperUserManager) {
    suspend operator fun invoke(limit: Int) {
        superUserManager.getSuperUser().setUsersLimit(limit)
    }
}