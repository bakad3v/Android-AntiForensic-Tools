package com.oasisfeng.island.routers

import androidx.navigation.NavController
import com.oasisfeng.island.R
import com.sonozaki.splash.domain.router.SplashRouter
import javax.inject.Inject

class SplashRouterImpl @Inject constructor(): SplashRouter {
    override fun enterPassword(controller: NavController) {
        controller.navigate(R.id.action_splashFragment_to_passFragmentNav)
    }

    override fun createPassword(controller: NavController) {
        controller.navigate(R.id.action_splashFragment_to_setupPassFragment)
    }
}