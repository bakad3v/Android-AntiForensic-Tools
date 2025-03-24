package com.sonozaki.passwordsetup.domain.repository

interface PasswordSetupRepository {
    suspend fun setPassword(password: CharArray)
}