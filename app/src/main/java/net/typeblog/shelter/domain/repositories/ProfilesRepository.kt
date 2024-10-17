package net.typeblog.shelter.domain.repositories

import net.typeblog.shelter.domain.entities.ProfileDomain
import net.typeblog.shelter.superuser.superuser.SuperUserException
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.Throws

/**
 * Repository for storing profiles data
 */
interface ProfilesRepository {

    /**
     * Function for getting all user profiles except the main one. Profiles saved in datastore are marked for deletion.
     */
    fun getProfiles(): Flow<List<ProfileDomain>?>

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
    @Throws(SuperUserException::class)
    suspend fun refreshDeviceProfiles()
}