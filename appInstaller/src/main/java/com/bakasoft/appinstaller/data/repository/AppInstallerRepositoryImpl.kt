package com.bakasoft.appinstaller.data.repository

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import com.bakasoft.appinstaller.data.network.DownloadAppFileService
import com.bakasoft.appinstaller.domain.repository.AppInstallerServiceRepository
import com.bakasoft.appinstaller.service.ACTION_INSTALL_RESULT
import com.bakasoft.appinstaller.service.EXTRA_SESSION_ID
import com.bakasoft.appinstaller.service.InstallResultReceiver
import com.bakasoft.network.RequestResult
import com.bakasoft.network.safeApiCall
import com.sonozaki.entities.AppInstallerData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import okhttp3.ResponseBody
import javax.inject.Inject

class AppInstallerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val _appInstallerDataDlow: MutableSharedFlow<AppInstallerData>,
    private val downloadAppFileService: DownloadAppFileService,
): AppInstallerServiceRepository {


    override suspend fun setInstallerData(data: AppInstallerData) {
        _appInstallerDataDlow.emit(data)
    }

    override suspend fun getAppInstallatorData(): AppInstallerData {
        return _appInstallerDataDlow.first()
    }

    override suspend fun downloadUpdate(path: String): RequestResult<ResponseBody> {
        return safeApiCall {
            downloadAppFileService.downloadAppTestVersion(path)
        }
    }

    override fun installUsualApp(response: ResponseBody) {
        val installer = context.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(
            PackageInstaller.SessionParams.MODE_FULL_INSTALL
        ).apply {
            val size = response.contentLength()
            if (size > 0) setSize(size)
        }

        val sessionId = installer.createSession(params)

        installer.openSession(sessionId).use { session ->
            val lengthHint = response.contentLength().takeIf { it > 0 } ?: -1L
            response.byteStream().use { inStream ->
                session.openWrite("base.apk", 0, lengthHint).use { out ->
                    inStream.copyTo(out)
                    session.fsync(out)
                }
            }

            val resultIntent = Intent(context, InstallResultReceiver::class.java).apply {
                action = ACTION_INSTALL_RESULT
                putExtra(EXTRA_SESSION_ID, sessionId)
            }
            val piFlags =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else
                    PendingIntent.FLAG_UPDATE_CURRENT
            val statusReceiver = PendingIntent
                .getBroadcast(context, 0, resultIntent, piFlags)
                .intentSender

            session.commit(statusReceiver)
        }
    }

}