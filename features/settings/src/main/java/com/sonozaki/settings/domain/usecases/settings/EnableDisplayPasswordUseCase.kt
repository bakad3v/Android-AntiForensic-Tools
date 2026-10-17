package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.superuser.superuser.SuperUserManager
import javax.inject.Inject

class EnableDisplayPasswordUseCase @Inject constructor(
    private val superUserManager: SuperUserManager
) {
    suspend operator fun invoke() {
        superUserManager.getSuperUser().enableDisplayPassword()
    }
}
