package com.bakad3v.dialogs.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DialogsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DialogsRepository {

    private val Context.dialogPreferences by preferencesDataStore(PREFERENCES_NAME)

    override val adminBruteforceWarningDisabled = context.dialogPreferences.data.map {
        preferences -> preferences[ADMIN_BRUTEFORCE_WARNING_DISABLED] ?: false
    }

    override suspend fun setAdminBruteforceWarningDisabled(disabled: Boolean) {
        context.dialogPreferences.edit { preferences ->
            preferences[ADMIN_BRUTEFORCE_WARNING_DISABLED] = disabled
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "dialogs"
        private val ADMIN_BRUTEFORCE_WARNING_DISABLED =
            booleanPreferencesKey("admin_bruteforce_warning_disabled")
    }
}
