package com.android.aftools.domain.usecases

import com.android.aftools.domain.repository.MainActivityRepository
import com.bakasoft.network.RequestResult
import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.entities.UISettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckTestOnlyStatusTest {
    @Test
    fun cleanTestOnlyInstall_initializesStatusWithoutRemovingAdmin() = runBlocking {
        val repository = FakeMainActivityRepository(
            currentStatus = true,
            savedStatus = null,
            appUpdated = false
        )

        CheckTestOnlyStatus(repository)()

        assertEquals(0, repository.disableAdminCalls)
        assertEquals(listOf(true), repository.savedStatuses)
    }

    @Test
    fun legacyTestOnlyUpdate_removesAdminAndSavesStatus() = runBlocking {
        val repository = FakeMainActivityRepository(
            currentStatus = true,
            savedStatus = null,
            appUpdated = true
        )

        CheckTestOnlyStatus(repository)()

        assertEquals(1, repository.disableAdminCalls)
        assertEquals(listOf(true), repository.savedStatuses)
    }

    @Test
    fun knownUsualToTestOnlyTransition_removesAdminAndSavesStatus() = runBlocking {
        val repository = FakeMainActivityRepository(
            currentStatus = true,
            savedStatus = false,
            appUpdated = true
        )

        CheckTestOnlyStatus(repository)()

        assertEquals(1, repository.disableAdminCalls)
        assertEquals(listOf(true), repository.savedStatuses)
    }

    @Test
    fun knownTestOnlyUpdate_keepsAdminRights() = runBlocking {
        val repository = FakeMainActivityRepository(
            currentStatus = true,
            savedStatus = true,
            appUpdated = true
        )

        CheckTestOnlyStatus(repository)()

        assertEquals(0, repository.disableAdminCalls)
        assertTrue(repository.savedStatuses.isEmpty())
    }

    @Test
    fun failedAdminRemoval_doesNotSaveTestOnlyStatus() = runBlocking {
        val repository = FakeMainActivityRepository(
            currentStatus = true,
            savedStatus = false,
            appUpdated = true,
            adminRemovalSucceeds = false
        )

        CheckTestOnlyStatus(repository)()

        assertEquals(1, repository.disableAdminCalls)
        assertTrue(repository.savedStatuses.isEmpty())
    }

    @Test
    fun usualBuild_savesUsualStatusWithoutRemovingAdmin() = runBlocking {
        val repository = FakeMainActivityRepository(
            currentStatus = false,
            savedStatus = null,
            appUpdated = true
        )

        CheckTestOnlyStatus(repository)()

        assertEquals(0, repository.disableAdminCalls)
        assertEquals(listOf(false), repository.savedStatuses)
    }

    private class FakeMainActivityRepository(
        private val currentStatus: Boolean,
        private val savedStatus: Boolean?,
        private val appUpdated: Boolean,
        private val adminRemovalSucceeds: Boolean = true
    ) : MainActivityRepository {
        var disableAdminCalls = 0
            private set
        val savedStatuses = mutableListOf<Boolean>()

        override val uiSettings: Flow<UISettings> = emptyFlow()
        override val appLatestData: Flow<RequestResult<AppLatestVersion>> = emptyFlow()
        override val displayUpdateNotification: Flow<Boolean> = emptyFlow()
        override val testOnlyNeeded: Flow<Boolean> = emptyFlow()

        override suspend fun checkUpdates() = Unit

        override suspend fun disableAdmin(): Boolean {
            disableAdminCalls++
            return adminRemovalSucceeds
        }

        override suspend fun savedTestOnlyStatus(): Boolean? = savedStatus

        override suspend fun saveTestOnlyStatus(status: Boolean) {
            savedStatuses += status
        }

        override fun isTestOnly(): Boolean = currentStatus

        override fun isAppUpdated(): Boolean = appUpdated
    }
}
