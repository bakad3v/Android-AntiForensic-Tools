package com.sonozaki.rootcommands.domain.repository

import kotlinx.coroutines.flow.Flow

interface RootScreenRepository {
    suspend fun setRootCommands(commands: String)
    suspend fun getRootCommands(): String
    suspend fun getRootPermission(): Boolean
    val runRootEnabled: Flow<Boolean>
    suspend fun setRunRoot(status: Boolean)
}