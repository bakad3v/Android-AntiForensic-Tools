package com.sonozaki.root.repository

import android.content.Context
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.root.entities.RootDomain
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RootRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    rootSerializer: BaseSerializer<RootDomain>):
    RootRepository {
       private val Context.rootCommandDatastore by encryptedDataStore(
           NAME,
           rootSerializer,
           alias = EncryptionAlias.DATASTORE.name,
           isDBA = true
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