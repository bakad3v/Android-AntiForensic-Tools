package com.bakasoft.setupwizard.domain.routers

import androidx.navigation.NavController

interface SetupWizardRouter {
    fun openUpdateCenter(navController: NavController)
    fun openSettings(navController: NavController)
    fun openProfiles(navController: NavController)
    fun openFiles(navController: NavController)
    fun openRoot(navController: NavController)
}