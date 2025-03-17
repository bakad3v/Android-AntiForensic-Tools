package com.android.aftools.routers

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.navArgs
import com.android.aftools.R
import com.sonozaki.passwordsetup.domain.router.PasswordSetupRouter
import com.sonozaki.passwordsetup.presentation.fragment.SetupPassFragmentArgs

class PasswordSetupRouterImpl: PasswordSetupRouter {
    override fun getFromSplash(fragment: Fragment): Boolean {
        return fragment.navArgs<SetupPassFragmentArgs>().value.fromSplash
    }

    override fun openNextScreen(navController: NavController) {
        navController.navigate(R.id.action_setupPassFragment_to_settingsFragment)
    }
}