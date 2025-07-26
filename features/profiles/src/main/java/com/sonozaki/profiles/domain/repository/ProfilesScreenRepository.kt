package com.sonozaki.profiles.domain.repository

import com.sonozaki.entities.Permissions
import com.sonozaki.entities.ProfileDomain
import kotlinx.coroutines.flow.Flow

interface ProfilesScreenRepository {
    suspend fun refreshDeviceProfiles()
    suspend fun setDeleteProfiles(new: Boolean)
    suspend fun setProfileDeletionStatus(id: Int, status: Boolean)
    suspend fun stopProfile(id: Int, isCurrent: Boolean)
    fun getProfiles(): Flow<List<ProfileDomain>?>
    val deleteProfiles: Flow<Boolean>
    val permissions: Flow<Permissions>
}