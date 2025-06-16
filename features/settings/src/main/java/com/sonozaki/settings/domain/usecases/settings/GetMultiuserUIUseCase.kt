package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.superuser.superuser.SuperUserManager
import javax.inject.Inject

class GetMultiuserUIUseCase @Inject constructor(private val superUserManager: SuperUserManager) {
    suspend operator fun invoke(): Boolean {
        return superUserManager.getSuperUser().getMultiuserUIStatus()
    }
}