package com.android.aftools.adapters

import com.sonozaki.data.profiles.repository.ProfilesRepository
import com.sonozaki.data.settings.repositories.PermissionsRepository
import com.sonozaki.data.settings.repositories.SettingsRepository
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.ProfileDomain
import com.sonozaki.profiles.domain.repository.ProfilesScreenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfilesAdapter @Inject constructor(
    private val profilesRepository: ProfilesRepository,
    private val settingsRepository: SettingsRepository,
    private val permissionsRepository: PermissionsRepository
): ProfilesScreenRepository {
    override suspend fun refreshDeviceProfiles() {
        profilesRepository.refreshDeviceProfiles()
    }

    override suspend fun setDeleteProfiles(new: Boolean) {
        settingsRepository.setDeleteProfiles(new)
    }

    override suspend fun setProfileDeletionStatus(id: Int, status: Boolean) {
        profilesRepository.setProfileDeletionStatus(id, status)
    }

    override suspend fun stopProfile(id: Int, isCurrent: Boolean) {
        profilesRepository.stopProfile(id, isCurrent)
    }

    override fun getProfiles(): Flow<List<ProfileDomain>?>
        = profilesRepository.getProfiles()
    override val deleteProfiles: Flow<Boolean>
        get() = settingsRepository.settings.map { it.deleteProfiles }
    override val permissions: Flow<Permissions>
        get() = permissionsRepository.permissions
}