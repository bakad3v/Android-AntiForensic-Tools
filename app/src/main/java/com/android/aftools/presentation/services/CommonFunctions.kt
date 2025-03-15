package com.android.aftools.presentation.services

import android.content.Context
import com.android.aftools.domain.entities.Settings
import com.android.aftools.superuser.superuser.SuperUser
import com.android.aftools.superuser.superuser.SuperUserException
import com.android.aftools.superuser.superuser.SuperUserManager

suspend fun Context.destroyApp(
    settings: Settings,
    superUser: SuperUser,
    isAdmin: Boolean = false,
    superUserManager: SuperUserManager,
    handleException: suspend (e: String) -> Unit
) {
    if (settings.removeItself) {
        superUser.uninstallApp(packageName)
    }
    if (settings.clearItself) {
        superUser.clearAppData(packageName)
    }
    if (settings.clearData) {
        if (isAdmin) {
            try {
                superUserManager.removeAdminRights()
            } catch (e: SuperUserException) {
                handleException(e.messageForLogs.asString(this))
            }
        }
        clearData(settings.hideItself) {
            handleException(it)
        }
    }
    if (settings.hideItself) {
        superUser.hideApp(packageName)
    }
}

suspend fun Context.clearData(hideItself: Boolean, handleException: suspend (e: String) -> Unit) {
    val dpsContext = createDeviceProtectedStorageContext()
    dpsContext.dataDir.listFiles()?.forEach { it.deleteRecursively() }
    try {
        dataDir.listFiles()?.forEach { it.deleteRecursively() }
    } catch (e: Exception) {
        handleException(e.stackTraceToString())
    }
    if (!hideItself) {
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}

