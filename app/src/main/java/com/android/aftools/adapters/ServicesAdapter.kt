package com.android.aftools.adapters

import android.net.Uri
import com.android.aftools.mappers.FileMapper
import com.sonozaki.data.files.repository.FilesRepository
import com.sonozaki.data.profiles.repository.ProfilesRepository
import com.sonozaki.data.settings.repositories.SettingsRepository
import com.sonozaki.entities.App
import com.sonozaki.entities.LogsData
import com.sonozaki.entities.Settings
import com.sonozaki.data.logs.repository.LogsRepository
import com.sonozaki.data.settings.repositories.DeviceProtectionSettingsRepository
import com.sonozaki.root.repository.RootRepository
import com.sonozaki.services.domain.entities.FileDomain
import com.sonozaki.services.domain.repository.ServicesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ServicesAdapter @Inject constructor(
    private val logsRepository: LogsRepository,
    private val profilesRepository: ProfilesRepository,
    private val rootRepository: RootRepository,
    private val settingsRepository: SettingsRepository,
    private val filesRepository: FilesRepository,
    private val deviceProtectionSettingsRepository: DeviceProtectionSettingsRepository,
    private val filesMapper: FileMapper
) : ServicesRepository {
    override suspend fun writeToLogs(text: String) {
        logsRepository.writeToLogs(text)
    }

    override suspend fun getLogsData(): LogsData {
        return logsRepository.getLogsData().first()
    }

    override suspend fun removeProfileFromDeletion(profile: Int) {
        profilesRepository.setProfileDeletionStatus(profile, false)
    }

    override fun getManagedApps(): List<App> {
        return listOf()
    }

    override suspend fun getRebootEnabled(): Boolean {
        return deviceProtectionSettingsRepository.deviceProtectionSettings.first().rebootOnLock
    }

    override suspend fun getRootCommand(): String {
        return rootRepository.getRootCommand().first()
    }

    override suspend fun getProfilesToDelete(): List<Int> {
        return profilesRepository.getProfilesToDelete().first()
    }

    override suspend fun getSettings(): Settings {
        return settingsRepository.settings.first()
    }

    override suspend fun getFiles(): List<FileDomain> {
        return filesMapper.mapDatastoreListToDomainList(filesRepository.getFiles().first())
    }

    override suspend fun deleteMyFile(uri: Uri) {
        filesRepository.deleteMyFile(uri)
    }

    override suspend fun setRunOnBoot() {
        settingsRepository.setRunOnBoot(true)
    }

    override suspend fun removeApplication(packageName: String) {

    }
}