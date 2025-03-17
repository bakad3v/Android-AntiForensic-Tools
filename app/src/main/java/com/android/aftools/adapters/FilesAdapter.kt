package com.android.aftools.adapters

import android.net.Uri
import com.android.aftools.mappers.FileMapper
import com.sonozaki.entities.FilesSortOrder
import com.sonozaki.files.domain.entities.FileInfo
import com.sonozaki.files.domain.repository.FilesScreenRepository
import com.sonozaki.files.repository.FilesRepository
import com.sonozaki.settings.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FilesAdapter @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val filesRepository: FilesRepository,
    private val fileMapper: FileMapper
    ): FilesScreenRepository {
    override val deletionEnabled: Flow<Boolean>
        get() = settingsRepository.settings.map { it.deleteFiles }
    override val sortOrder: StateFlow<FilesSortOrder>
        get() = filesRepository.getSortOrder()
    override val files: Flow<List<FileInfo>>
        get() = filesRepository.getFiles().map { fileMapper.mapDatastoreListToInfoList(it) }

    override suspend fun changeFilePriority(priority: Int, uri: Uri) {
        filesRepository.changeFilePriority(priority, uri)
    }

    override suspend fun changeSortOrder(sortOrder: FilesSortOrder) {
        filesRepository.changeSortOrder(sortOrder)
    }

    override suspend fun insertMyFile(uri: Uri, isDirectory: Boolean) {
        filesRepository.insertMyFile(uri, isDirectory)
    }

    override suspend fun clearFiles() {
        filesRepository.clearFiles()
    }

    override suspend fun deleteMyFile(uri: Uri) {
        filesRepository.deleteMyFile(uri)
    }

    override suspend fun setDeleteFiles(new: Boolean) {
        settingsRepository.setDeleteFiles(new)
    }
}