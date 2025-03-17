package com.sonozaki.profiles.repository

import android.content.Context
import com.sonozaki.encrypteddatastore.datastoreDBA.dataStoreDirectBootAware
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.ProfileDomain
import com.sonozaki.profiles.entities.IntList
import com.sonozaki.profiles.mapper.ProfilesMapper
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.superuser.superuser.SuperUserManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfilesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profilesMapper: ProfilesMapper,
    private val superUserManager: SuperUserManager,
    private val profilesOnDevice: MutableSharedFlow<List<ProfileDomain>?>,
    private val coroutineScope: CoroutineScope,
    profilesSerializer: EncryptedSerializer<IntList>
) : ProfilesRepository {

    private val Context.profilesDatastore by dataStoreDirectBootAware(
        DATASTORE_NAME,
        profilesSerializer
    )

    companion object {
        private const val DATASTORE_NAME = "profiles_datastore.json"
    }



    override fun getProfiles(): Flow<List<ProfileDomain>?> {
        coroutineScope.launch {
            refreshDeviceProfiles()
        }
        return combine(context.profilesDatastore.data, profilesOnDevice) { toDelete, profiles -> profilesMapper.mapToProfilesWithStatus(profiles,toDelete)}
    }

    @Throws(SuperUserException::class)
    override suspend fun refreshDeviceProfiles() {
        coroutineScope.launch(Dispatchers.IO) {
            profilesOnDevice.emit(
                try {
                    superUserManager.getSuperUser().getProfiles().filter { it.id != 0 }
                } catch (e: SuperUserException) {
                    null
                }
            )
        }
    }

    override fun getProfilesToDelete() : Flow<List<Int>> = context.profilesDatastore.data.map { it.list }

    override suspend fun setProfileDeletionStatus(id: Int, status: Boolean) {
        context.profilesDatastore.updateData {
            if (status) {
                it.add(id)
            } else {
                it.delete(id)
            }
        }
    }
}

