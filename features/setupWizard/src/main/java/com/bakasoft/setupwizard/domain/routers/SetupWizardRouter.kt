package com.bakasoft.setupwizard.domain.routers

import androidx.navigation.NavController

interface SetupWizardRouter {
    fun openUpdateCenter(navController: NavController)
    fun openSettings(navController: NavController)
    fun openDataDestructionSettings(navController: NavController)
    fun openPermissionSettings(navController: NavController)
    fun openTriggerSettings(navController: NavController)
    fun openPermanentSettings(navController: NavController)
    fun openNotificationSettings(navController: NavController)
    fun openMultiuserSettings(navController: NavController)
    fun openProfiles(navController: NavController)
    fun openFiles(navController: NavController)
    fun openRoot(navController: NavController)
}
