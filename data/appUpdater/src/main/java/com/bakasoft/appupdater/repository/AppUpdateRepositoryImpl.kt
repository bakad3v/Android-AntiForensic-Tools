package com.bakasoft.appupdater.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.bakasoft.appupdater.network.DownloadAppsLastVersionService
import com.bakasoft.network.NetworkError
import com.bakasoft.network.RequestResult
import com.bakasoft.network.safeApiCall
import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.resources.APP_FLAVOR
import com.sonozaki.resources.APP_VERSION
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named


class AppUpdateRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadAppsLastVersionService: DownloadAppsLastVersionService,
    @Named(APP_VERSION) private val appVersion: String,
    @Named(APP_FLAVOR) private val appFlavor: String,
    private val _appUpdateDataFlow: MutableSharedFlow<RequestResult<AppLatestVersion>>):
    AppUpdateRepository {

   companion object {
       private const val PREFERENCE_NAME = "app_update_repository"
       private val UPDATE_POPUP_USER_PREFERENCE = booleanPreferencesKey("show_update_popup")
   }

    private val Context.showUpdatePopupUserPreference by preferencesDataStore(PREFERENCE_NAME)

    override val appUpdateDataFlow = _appUpdateDataFlow.asSharedFlow()

    private fun Context.isTestOnlyApp(): Boolean {
        return (applicationInfo.flags and ApplicationInfo.FLAG_TEST_ONLY) != 0
    }

    private fun parseAppVersion(data: String): String? {
        val regex = """versionName\s*=\s*"([^"]+)"""".toRegex()
        return regex.find(data)?.groupValues?.get(1)
    }

    private fun createAppDownloadLinks(version: String): AppLatestVersion {
        Log.w("isTestOnly",context.isTestOnlyApp().toString())
        return AppLatestVersion(
            "https://github.com/bakad3v/Android-AntiForensic-Tools/releases/download/v$version/AFTools_${appFlavor}_NON_ROOT_ONLY.apk",
            "https://github.com/bakad3v/Android-AntiForensic-Tools/releases/download/v$version/AFTools_${appFlavor}_TESTONLY.apk",
            version != appVersion,
            context.isTestOnlyApp(),
            version
        )
    }

    override suspend fun checkUpdates() {
        val result = safeApiCall {
            downloadAppsLastVersionService.downloadAppLastVersion()
        }
        when (result) {
            is RequestResult.Data -> {
                val version = parseAppVersion(result.data.string())
                if (version == null) {
                    _appUpdateDataFlow.emit(
                        RequestResult.Error(NetworkError.EmptyResponse)
                    )
                } else {
                    _appUpdateDataFlow.emit(
                        RequestResult.Data(
                            createAppDownloadLinks(version)
                        )
                    )
                }
            }
            is RequestResult.Error -> _appUpdateDataFlow.emit(
                RequestResult.Error(result.error)
            )
        }
    }

    override val showUpdatePopupStatus: Flow<Boolean>
        get() = context.showUpdatePopupUserPreference.data.map { it[UPDATE_POPUP_USER_PREFERENCE] != false }

    override suspend fun setUpdatePopupStatus(status: Boolean) {
        context.showUpdatePopupUserPreference.edit {
            it[UPDATE_POPUP_USER_PREFERENCE] = status
        }
    }
}