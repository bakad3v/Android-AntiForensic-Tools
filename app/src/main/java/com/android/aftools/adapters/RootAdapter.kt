package com.android.aftools.adapters

import com.sonozaki.root.repository.RootRepository
import com.sonozaki.rootcommands.domain.repository.RootScreenRepository
import com.sonozaki.settings.repositories.PermissionsRepository
import com.sonozaki.settings.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RootAdapter @Inject constructor(private val rootRepository: RootRepository,
    private val settingsRepository: SettingsRepository,
    private val permissionsRepository: PermissionsRepository): RootScreenRepository {
    override suspend fun setRootCommands(commands: String) {
        rootRepository.setRootCommand(commands)
    }

    override suspend fun getRootCommands(): String {
        return rootRepository.getRootCommand().first()
    }

    override suspend fun getRootPermission(): Boolean {
        return permissionsRepository.permissions.first().isRoot
    }

    override suspend fun setRunRoot(status: Boolean) {
        settingsRepository.setRunRoot(status)
    }

    override val runRootEnabled: Flow<Boolean>
        get() = settingsRepository.settings.map { it.runRoot }

}