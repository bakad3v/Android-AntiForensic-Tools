package com.android.aftools.adapters

import com.sonozaki.password.repository.PasswordManager
import com.sonozaki.splash.domain.repository.SplashRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SplashAdapter @Inject constructor(private val passwordManager: PasswordManager): SplashRepository {
    override fun getPasswordStatus(): Flow<Boolean> {
        return passwordManager.passwordStatus
    }
}