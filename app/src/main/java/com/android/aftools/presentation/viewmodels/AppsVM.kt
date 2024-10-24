package com.android.aftools.presentation.viewmodels

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.lifecycle.ViewModel


class AppsVM: ViewModel() {
    private fun getInstalledApplication(context: Context): List<ApplicationInfo> {
        val installedApps: List<ApplicationInfo> = context.packageManager.getInstalledApplications(0)
        return installedApps
    }
}