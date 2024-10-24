package com.android.aftools.domain.repositories

import kotlinx.coroutines.flow.Flow
/*
* Repository for storing custom root command
*/
interface RootRepository {
    fun getRootCommand(): Flow<String>
    suspend fun setRootCommand(command: String)
}