package com.sonozaki.data.profiles.repository

import com.sonozaki.entities.ProfileDomain
import kotlinx.coroutines.flow.Flow

/**
 * Repository for storing profiles data
 */
interface ProfilesRepository {

    /**
     * Function for getting all user profiles except the main one. Profiles saved in datastore are marked for deletion.
     */
    fun getProfiles(): Flow<List<ProfileDomain>?>

    /**
     * Function for stopping selected profile
     */
    suspend fun stopProfile(id: Int, isCurrent: Boolean)

    /**
     * Function for marking or unmarking profiles for deletion.
     */
    suspend fun setProfileDeletionStatus(id: Int, status: Boolean)

    /**
     * Function for getting ids of profiles marked for deletion.
     */
    fun getProfilesToDelete(): Flow<List<Int>>
    /**
     * Function for retrieving information about user profiles from device. Requires root or dhizuku rights.
     */
    suspend fun refreshDeviceProfiles()
}