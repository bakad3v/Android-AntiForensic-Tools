package com.sonozaki.data.profiles.repository

import android.content.Context
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.data.profiles.entities.IntList
import com.sonozaki.data.profiles.mapper.ProfilesMapper
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.entities.ProfileDomain
import com.sonozaki.resources.IO_DISPATCHER
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.superuser.superuser.SuperUserManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class ProfilesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profilesMapper: ProfilesMapper,
    private val superUserManager: SuperUserManager,
    private val profilesOnDevice: MutableSharedFlow<List<ProfileDomain>?>,
    @Named(IO_DISPATCHER) private val coroutineDispatcher: CoroutineDispatcher,
    profilesSerializer: BaseSerializer<IntList>
) : ProfilesRepository {

    private val Context.profilesDatastore by encryptedDataStore(
        DATASTORE_NAME,
        profilesSerializer,
        alias = EncryptionAlias.DATASTORE.name,
        isDBA = true
    )

    companion object {
        private const val DATASTORE_NAME = "profiles_datastore.json"
    }


    override fun getProfiles(): Flow<List<ProfileDomain>?> {
        return combine(profilesOnDevice, context.profilesDatastore.data) { profiles, toDelete ->
            profilesMapper.mapToProfilesWithStatus(profiles, toDelete)
        }
    }

    override suspend fun stopProfile(id: Int, isCurrent: Boolean) {
        withContext(coroutineDispatcher) {
            superUserManager.getSuperUser().stopProfile(id, isCurrent)
        }
    }

    @Throws(SuperUserException::class)
    override suspend fun refreshDeviceProfiles() {
        withContext(coroutineDispatcher) {
            profilesOnDevice.emit(
                try {
                    superUserManager.getSuperUser().getProfiles().filter { it.id != 0 }
                } catch (e: SuperUserException) {
                    null
                }
            )
        }
    }

    override fun getProfilesToDelete(): Flow<List<Int>> =
        context.profilesDatastore.data.map { it.list }

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

