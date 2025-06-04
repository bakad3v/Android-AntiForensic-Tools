package com.bakasoft.appupdater.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.bakasoft.appupdater.network.DownloadAppTestVersionService
import com.bakasoft.network.RequestResult
import com.bakasoft.network.safeApiCall
import com.sonozaki.resources.APP_UPDATE_URL
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Named


class AppUpdateRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    val downloadAppTestVersionService: DownloadAppTestVersionService,
    @Named(APP_UPDATE_URL) private val downloadPath: String) :
    AppUpdateRepository {

   companion object {
       private const val PREFERENCE_NAME = "app_update_repository"
       private val UPDATE_POPUP_USER_PREFERENCE = booleanPreferencesKey("show_update_popup")
   }

    private val Context.showUpdatePopupUserPreference by preferencesDataStore(PREFERENCE_NAME)

    private fun Context.isTestOnlyApp(): Boolean {
        return (applicationInfo.flags and ApplicationInfo.FLAG_TEST_ONLY) != 0
    }

    override val showUpdatePopupStatus: Flow<Boolean>
        get() = context.showUpdatePopupUserPreference.data.map { it[UPDATE_POPUP_USER_PREFERENCE] != false && !context.isTestOnlyApp() }

    override suspend fun disableUpdatePopup() {
        context.showUpdatePopupUserPreference.edit {
            it[UPDATE_POPUP_USER_PREFERENCE] = false
        }
    }

    override suspend fun downloadUpdate(): RequestResult<ResponseBody> {
        return safeApiCall {
            downloadAppTestVersionService.downloadAppTestVersion(downloadPath)
        }
    }
}