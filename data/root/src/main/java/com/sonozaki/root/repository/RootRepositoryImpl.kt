package com.sonozaki.root.repository

import android.content.Context
import androidx.datastore.deviceProtectedDataStore
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.root.entities.RootDomain
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RootRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    rootSerializer: EncryptedSerializer<RootDomain>):
    RootRepository {
       private val Context.rootCommandDatastore by deviceProtectedDataStore(
           NAME,
           rootSerializer
       )

    override fun getRootCommand() = context.rootCommandDatastore.data.map { it.rootCommand }

    override suspend fun setRootCommand(command: String) {
        context.rootCommandDatastore.updateData {
            RootDomain(command)
        }
    }

        companion object  {
        private const val NAME = "root_command.json"
    }
}