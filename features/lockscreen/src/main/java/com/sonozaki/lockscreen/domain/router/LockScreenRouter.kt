package com.sonozaki.lockscreen.domain.router

import androidx.navigation.NavController

interface LockScreenRouter {
    fun openNextScreen(controller: NavController)
}