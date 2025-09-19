package com.android.aftools.routers

import androidx.navigation.NavController
import com.android.aftools.R
import com.sonozaki.lockscreen.domain.router.LockScreenRouter
import javax.inject.Inject

class LockScreenRouterImpl @Inject constructor(): LockScreenRouter {
    override fun openNextScreen(controller: NavController) {
        controller.navigate(R.id.action_passFragmentNav_to_wizardFragment)
    }
}