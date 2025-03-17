package com.android.aftools.adapters

import com.sonozaki.password.repository.PasswordManager
import com.sonozaki.passwordsetup.domain.repository.PasswordSetupRepository
import javax.inject.Inject

class PasswordSetupAdapter @Inject constructor(private val passwordManager: PasswordManager): PasswordSetupRepository {
    override suspend fun setPassword(password: CharArray) {
        passwordManager.setPassword(password)
    }
}