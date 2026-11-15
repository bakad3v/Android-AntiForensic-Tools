package com.sonozaki.superuser.root

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MagiskManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun launchIntent(): Intent? = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)

    /** Magisk 31.1 moved permission grants into the manager app. */
    @Suppress("DEPRECATION")
    fun permissionIntent(): Intent? {
        val packageManager = context.packageManager
        val packageInfo = try {
            packageManager.getPackageInfo(PACKAGE_NAME, 0)
        } catch (_: PackageManager.NameNotFoundException) {
            return null
        }
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
        return if (versionCode >= MANUAL_GRANT_VERSION_CODE) {
            launchIntent()
        } else {
            null
        }
    }

    companion object {
        private const val PACKAGE_NAME = "com.topjohnwu.magisk"
        private const val MANUAL_GRANT_VERSION_CODE = 31100L
    }
}
