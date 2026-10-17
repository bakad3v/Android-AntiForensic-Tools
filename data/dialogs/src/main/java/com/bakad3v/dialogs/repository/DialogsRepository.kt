package com.bakad3v.dialogs.repository

import kotlinx.coroutines.flow.Flow

interface DialogsRepository {
    val adminBruteforceWarningDisabled: Flow<Boolean>

    suspend fun setAdminBruteforceWarningDisabled(disabled: Boolean)
}
