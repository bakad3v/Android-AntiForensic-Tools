package com.sonozaki.passwordsetup.domain.router

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.sonozaki.passwordsetup.presentation.fragment.SetupPassFragment

interface PasswordSetupRouter {
    fun getFromSplash(fragment: Fragment): Boolean
    fun openNextScreen(navController: NavController)
}