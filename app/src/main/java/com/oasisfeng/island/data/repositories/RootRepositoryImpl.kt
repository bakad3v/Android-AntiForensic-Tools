package com.oasisfeng.island.data.repositories

import android.content.Context
import com.oasisfeng.island.data.serializers.RootSerializer
import com.oasisfeng.island.datastoreDBA.dataStoreDirectBootAware
import com.oasisfeng.island.domain.entities.RootDomain
import com.oasisfeng.island.domain.repositories.RootRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RootRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context, private val rootSerializer: RootSerializer): RootRepository {
       private val Context.rootCommandDatastore by dataStoreDirectBootAware(NAME, rootSerializer)

    override fun getRootCommand() = context.rootCommandDatastore.data.map { it.rootCommand }

    override suspend fun setRootCommand(command: String) {
        context.rootCommandDatastore.updateData {
            RootDomain(command)
        }
    }

        companion object  {
        private val NAME = "root_command.json"
    }
}