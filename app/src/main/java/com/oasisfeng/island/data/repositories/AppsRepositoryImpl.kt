package com.oasisfeng.island.data.repositories

import android.content.Context
import android.content.pm.PackageManager
import com.oasisfeng.island.data.mappers.AppsMapper
import com.oasisfeng.island.data.serializers.AppsSerializer
import com.oasisfeng.island.datastoreDBA.dataStoreDirectBootAware
import com.oasisfeng.island.domain.entities.AppDomain
import com.oasisfeng.island.domain.repositories.AppsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appsMapper: AppsMapper,
    appsSerializer: AppsSerializer
) : AppsRepository {

    private val Context.appsDatastore by dataStoreDirectBootAware(DATASTORE_NAME, appsSerializer)

    override fun getManagedApps(): Flow<List<AppDomain>> =
        context.appsDatastore.data.map { appsMapper.mapListDatastoreToListDt(context, it) }

    override fun getInstalledApplications(): List<AppDomain> {
        val installedApps: List<AppDomain> = context.packageManager.getInstalledPackages(
            PackageManager.GET_META_DATA
        ).map { appsMapper.mapPackageInfoToAppDT(context, it) }
        return installedApps
    }

    override suspend fun addApplications(apps: List<AppDomain>) {
        context.appsDatastore.updateData {
            it.addMultiple(appsMapper.mapDtListToDatastore(apps))
        }
    }

    override suspend fun setDeletionStatus(status: Boolean, packageName: String) {
        context.appsDatastore.updateData {
            it.setDeletionStatus(packageName, status)
        }
    }

    override suspend fun setHiddenStatus(status: Boolean, packageName: String) {
        context.appsDatastore.updateData {
            it.setHideStatus(packageName, status)
        }
    }

    override suspend fun setDataClearStatus(status: Boolean, packageName: String) {
        context.appsDatastore.updateData {
            it.setClearDataStatus(packageName, status)
        }
    }

    override suspend fun removeApplication(packageName: String) {
        context.appsDatastore.updateData {
            it.delete(packageName)
        }
    }

    override suspend fun clearDb() {
        context.appsDatastore.updateData { it.clear() }
    }

    companion object {
        private const val DATASTORE_NAME = "apps_datastore.json"
    }
}