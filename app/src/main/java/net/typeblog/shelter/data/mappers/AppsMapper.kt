package net.typeblog.shelter.data.mappers

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import net.typeblog.shelter.data.entities.AppDatastore
import net.typeblog.shelter.data.entities.AppList
import net.typeblog.shelter.domain.entities.AppDomain
import javax.inject.Inject


class AppsMapper @Inject constructor(){
    fun mapPackageInfoToAppDT(context: Context, packageInfo: PackageInfo): AppDomain = AppDomain(
        packageName = packageInfo.packageName,
        appName = packageInfo.applicationInfo.name,
        system = isSystemApp(packageInfo.applicationInfo),
        enabled = packageInfo.applicationInfo.enabled,
        icon = packageInfo.applicationInfo.loadIcon(context.packageManager)
    )

    private fun isSystemApp(ai: ApplicationInfo): Boolean {
        val mask = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
        return (ai.flags and mask) != 0
    }

    fun mapDtToDatastore(appDomain: AppDomain): AppDatastore = AppDatastore(
        packageName = appDomain.packageName,
        appName = appDomain.appName,
        system = appDomain.system,
        enabled = appDomain.enabled,
        toDelete = appDomain.toDelete,
        toHide = appDomain.toHide,
        toClearData = appDomain.toClearData
    )

    fun mapDtListToDatastore(apps: List<AppDomain>): List<AppDatastore> =
        apps.map { mapDtToDatastore(it) }


    fun mapDatastoreToDt(context: Context, appDatastore: AppDatastore): AppDomain = AppDomain(
        packageName = appDatastore.packageName,
        appName = appDatastore.appName,
        system = appDatastore.system,
        enabled = appDatastore.enabled,
        toHide = appDatastore.toHide,
        toDelete = appDatastore.toDelete,
        toClearData = appDatastore.toClearData,
        icon = context.packageManager.getPackageInfo(appDatastore.packageName, 0).applicationInfo.loadIcon(context.packageManager)
    )

    fun mapListDatastoreToListDt(context: Context, appList: AppList): List<AppDomain>  =
        appList.list.map { mapDatastoreToDt(context,it) }

}