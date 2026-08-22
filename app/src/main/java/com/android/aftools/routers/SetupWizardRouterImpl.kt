package com.android.aftools.routers

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.android.aftools.R
import com.bakasoft.setupwizard.domain.routers.SetupWizardRouter
import com.sonozaki.settings.navigation.SettingsNavigationTarget
import javax.inject.Inject

class SetupWizardRouterImpl @Inject constructor(): SetupWizardRouter {
    override fun openUpdateCenter(navController: NavController) {
        navController.navigate(R.id.appUpdaterFragment)
    }

    override fun openSettings(navController: NavController) {
        openSettings(navController, SettingsNavigationTarget.ROOT)
    }

    override fun openDataDestructionSettings(navController: NavController) {
        openSettings(navController, SettingsNavigationTarget.DATA_DESTRUCTION)
    }

    override fun openPermissionSettings(navController: NavController) {
        openSettings(navController, SettingsNavigationTarget.PERMISSIONS)
    }

    override fun openTriggerSettings(navController: NavController) {
        openSettings(navController, SettingsNavigationTarget.TRIGGERS)
    }

    override fun openPermanentSettings(navController: NavController) {
        openSettings(navController, SettingsNavigationTarget.PERMANENT)
    }

    override fun openNotificationSettings(navController: NavController) {
        openSettings(navController, SettingsNavigationTarget.NOTIFICATIONS)
    }

    override fun openMultiuserSettings(navController: NavController) {
        openSettings(navController, SettingsNavigationTarget.MULTIUSER)
    }

    private fun openSettings(navController: NavController, target: String) {
        navController.navigate(
            R.id.settingsGraph,
            bundleOf(SettingsNavigationTarget.ARGUMENT_NAME to target)
        )
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
