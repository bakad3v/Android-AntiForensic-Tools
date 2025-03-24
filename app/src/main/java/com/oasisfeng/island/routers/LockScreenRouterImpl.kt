package com.oasisfeng.island.routers

import androidx.navigation.NavController
import com.oasisfeng.island.R
import com.sonozaki.lockscreen.domain.router.LockScreenRouter
import javax.inject.Inject

class LockScreenRouterImpl @Inject constructor(): LockScreenRouter {
    override fun openNextScreen(controller: NavController) {
        controller.navigate(R.id.action_passFragmentNav_to_settingsFragment)
    }
}