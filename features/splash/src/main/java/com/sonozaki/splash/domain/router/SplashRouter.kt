package com.sonozaki.splash.domain.router

import androidx.navigation.NavController

interface SplashRouter {
    fun enterPassword(controller: NavController)
    fun createPassword(controller: NavController)
}