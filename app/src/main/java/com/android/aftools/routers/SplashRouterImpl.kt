package com.android.aftools.routers

import androidx.navigation.NavController
import com.android.aftools.R
import com.sonozaki.splash.domain.router.SplashRouter

class SplashRouterImpl: SplashRouter {
    override fun enterPassword(controller: NavController) {
        controller.navigate(R.id.action_splashFragment_to_passFragmentNav)
    }

    override fun createPassword(controller: NavController) {
        controller.navigate(R.id.action_splashFragment_to_setupPassFragment)
    }
}