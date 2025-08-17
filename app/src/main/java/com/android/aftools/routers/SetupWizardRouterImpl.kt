package com.android.aftools.routers

import androidx.navigation.NavController
import com.android.aftools.R
import com.bakasoft.setupwizard.domain.routers.SetupWizardRouter
import javax.inject.Inject

class SetupWizardRouterImpl @Inject constructor(): SetupWizardRouter {
    override fun openUpdateCenter(navController: NavController) {
        navController.navigate(R.id.appUpdaterFragment)
    }

    override fun openSettings(navController: NavController) {
        navController.navigate(R.id.settingsGraph)
    }

    override fun openProfiles(navController: NavController) {
        navController.navigate(R.id.profilesFragment)
    }

    override fun openFiles(navController: NavController) {
        navController.navigate(R.id.setupFilesFragment)
    }

    override fun openRoot(navController: NavController) {
        navController.navigate(R.id.rootFragment)
    }
}