package com.sonozaki.splash.domain.repository

import kotlinx.coroutines.flow.Flow

interface SplashRepository {
    fun getPasswordStatus(): Flow<Boolean>
}