package com.oasisfeng.island.presentation.services

import android.content.Context
import com.oasisfeng.island.domain.entities.Settings
import com.oasisfeng.island.superuser.superuser.SuperUser
import com.oasisfeng.island.superuser.superuser.SuperUserException
import com.oasisfeng.island.superuser.superuser.SuperUserManager

suspend fun Context.destroyApp(settings: Settings, context: Context, superUser: SuperUser,isAdmin: Boolean=false,superUserManager: SuperUserManager) {
    if (settings.removeItself) {
        superUser.uninstallApp(context.packageName)
    }
    if (settings.clearItself) {
        superUser.clearAppData(context.packageName)
    }
    if (settings.clearData) {
        if (isAdmin) {
            try {
                superUserManager.removeAdminRights()
            } catch (e: SuperUserException) {
            }
        }
        context.clearData(settings.hideItself)
    }
    if (settings.hideItself) {
        superUser.hideApp(context.packageName)
    }
}

fun Context.clearData(hideItself: Boolean) {
    val dpsContext = createDeviceProtectedStorageContext()
    dpsContext.dataDir.listFiles().forEach {  it.deleteRecursively() }
    try {
        dataDir.listFiles().forEach { it.deleteRecursively() }
    } catch (e: Exception) {
    }
    if (!hideItself) {
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}

