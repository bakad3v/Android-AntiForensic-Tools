package com.android.aftools.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.android.aftools.datastoreDBA.preferencesDataStoreDirectBootAware
import com.android.aftools.domain.repositories.RootRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RootRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context): RootRepository {
       private val Context.rootCommandDatastore by preferencesDataStoreDirectBootAware(NAME)

    override fun getRootCommand() = context.rootCommandDatastore.data.map { it[COMMAND]?: "" }

    override suspend fun setRootCommand(command: String) {
        context.rootCommandDatastore.edit {
            it[COMMAND] = command
        }
    }

        companion object  {
        private val NAME = "root_command.json"
        private val COMMAND = stringPreferencesKey("COMMAND")
    }
}