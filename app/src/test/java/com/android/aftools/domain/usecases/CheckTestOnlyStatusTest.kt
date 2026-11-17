package com.android.aftools.domain.usecases

import com.android.aftools.domain.repository.DeviceAdminActivator
import com.android.aftools.domain.repository.MainActivityRepository
import com.bakasoft.network.RequestResult
import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.entities.UISettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CheckTestOnlyStatusTest {
    @Test
    fun cleanTestOnlyInstall_initializesStatusWithoutRemovingAdmin() = runBlocking {
        val repository = FakeMainActivityRepository(
            currentStatus = true,
            savedStatus = null,
            appUpdated = false
        )

        CheckTestOnlyStatus(repository)()

        assertEquals(0, repository.backendChecks)
        assertEquals(listOf(true), repository.savedStatuses)
    }

    @Test
    fun legacyTestOnlyUpdate_recreatesAdminAndSavesStatus() = runBlocking {
        val repository = FakeMainActivityRepository(
            currentStatus = true,
            savedStatus = null,
            appUpdated = true
        )

        CheckTestOnlyStatus(repository)()

        assertEquals(1, repository.backendChecks)
        assertEquals(listOf(true), repository.savedStatuses)
    }

    @Test
    fun knownUsualToTestOnlyTransition_recreatesAdminAndSavesStatus() = runBlocking {
        val repository = FakeMainActivityRepository(
            currentStatus = true,
            savedStatus = false,
            appUpdated = true
        )

        CheckTestOnlyStatus(repository)()

        assertEquals(1, repository.backendChecks)
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

        assertEquals(0, repository.backendChecks)
        assertTrue(repository.savedStatuses.isEmpty())
    }

    @Test
    fun failedAdminRecreation_doesNotSaveTestOnlyStatus() = runBlocking {
        val repository = FakeMainActivityRepository(
            currentStatus = true,
            savedStatus = false,
            appUpdated = true,
            adminRecreationSucceeds = false
        )

        CheckTestOnlyStatus(repository)()

        assertEquals(1, repository.backendChecks)
        assertTrue(repository.savedStatuses.isEmpty())
    }

    @Test
    fun incompleteAdminRecreation_isRetriedUntilItSucceeds() = runBlocking {
        val repository = FakeMainActivityRepository(
            currentStatus = true,
            savedStatus = false,
            appUpdated = true,
            adminRecreationSucceeds = false
        )
        val checkStatus = CheckTestOnlyStatus(repository)

        checkStatus()
        repository.adminRecreationSucceeds = true
        checkStatus()

        assertEquals(2, repository.backendChecks)
        assertEquals(listOf(true), repository.savedStatuses)
    }

    @Test
    fun usualBuild_savesUsualStatusWithoutRemovingAdmin() = runBlocking {
        val repository = FakeMainActivityRepository(
            currentStatus = false,
            savedStatus = null,
            appUpdated = true
        )

        CheckTestOnlyStatus(repository)()

        assertEquals(0, repository.backendChecks)
        assertEquals(listOf(false), repository.savedStatuses)
    }

    @Test
    fun stoppedBackend_keepsActiveAdminDespiteStoredPermission() = runTest {
        val repository = FakeMainActivityRepository(true, false, true).apply {
            backendAvailable = false
        }

        assertFalse(CheckTestOnlyStatus(repository)())

        assertEquals(listOf("preflight"), repository.events)
        assertTrue(repository.adminActive)
        assertFalse(repository.pending)
        assertTrue(repository.savedStatuses.isEmpty())
    }

    @Test
    fun migration_preflightsAndPersistsRecoveryBeforeRemovingAdmin() = runTest {
        val repository = FakeMainActivityRepository(true, false, true)

        assertTrue(CheckTestOnlyStatus(repository)())

        assertEquals(listOf("preflight", "pending:true", "remove", "activate", "pending:false"), repository.events)
        assertTrue(repository.adminActive)
        assertFalse(repository.pending)
    }

    @Test
    fun shizukuStartedLater_retriesInSameProcessAndStopsOnSuccess() = runTest {
        val repository = FakeMainActivityRepository(true, false, true).apply {
            backendAvailable = false
        }
        val job = launch { CheckTestOnlyStatus(repository).runUntilComplete() }
        runCurrent()
        assertTrue(repository.adminActive)
        assertEquals(0, repository.removalCalls)

        repository.backendAvailable = true
        advanceTimeBy(5_000)
        runCurrent()

        assertTrue(job.isCompleted)
        assertEquals(1, repository.removalCalls)
        assertEquals(1, repository.activationCalls)
        assertEquals(listOf(true), repository.savedStatuses)
        advanceTimeBy(30_000)
        runCurrent()
        assertEquals(1, repository.activationCalls)
    }

    @Test
    fun backendDiesAfterRemoval_retriesPendingActivationWithoutAnotherRemoval() = runTest {
        val repository = FakeMainActivityRepository(true, false, true, false)
        val job = launch { CheckTestOnlyStatus(repository).runUntilComplete() }
        runCurrent()
        assertTrue(repository.pending)
        assertFalse(repository.adminActive)
        assertTrue(repository.savedStatuses.isEmpty())

        repository.backendAvailable = false
        advanceTimeBy(5_000)
        runCurrent()
        assertTrue(repository.pending)
        assertEquals(1, repository.activationCalls)

        repository.backendAvailable = true
        repository.adminRecreationSucceeds = true
        advanceTimeBy(5_000)
        runCurrent()

        assertTrue(job.isCompleted)
        assertEquals(1, repository.removalCalls)
        assertEquals(2, repository.activationCalls)
        assertTrue(repository.adminActive)
        assertFalse(repository.pending)
        assertEquals(listOf(true), repository.savedStatuses)
    }

    @Test
    fun processRestart_resumesPersistedActivationWithoutAnotherRemoval() = runTest {
        val repository = FakeMainActivityRepository(true, false, true, false)
        assertFalse(CheckTestOnlyStatus(repository)())

        repository.adminRecreationSucceeds = true
        assertTrue(CheckTestOnlyStatus(repository)())

        assertEquals(1, repository.removalCalls)
        assertEquals(2, repository.activationCalls)
        assertTrue(repository.adminActive)
        assertFalse(repository.pending)
    }

    @Test
    fun lockedUser_doesNotAttemptMigration() = runTest {
        val repository = FakeMainActivityRepository(true, false, true).apply {
            userUnlocked = false
        }

        assertFalse(CheckTestOnlyStatus(repository)())

        assertTrue(repository.events.isEmpty())
        assertTrue(repository.adminActive)
        assertTrue(repository.savedStatuses.isEmpty())
    }

    @Test
    fun inactiveAdminWithoutPendingRecovery_doesNotGrantNewAdminRights() = runTest {
        val repository = FakeMainActivityRepository(true, false, true).apply {
            adminActive = false
            backendAvailable = false
        }

        assertTrue(CheckTestOnlyStatus(repository)())

        assertTrue(repository.events.isEmpty())
        assertFalse(repository.adminActive)
        assertEquals(listOf(true), repository.savedStatuses)
    }

    @Test
    fun activationWithoutActualAdminRights_doesNotClearPendingRecovery() = runTest {
        val repository = FakeMainActivityRepository(true, false, true).apply {
            activationTakesEffect = false
        }

        assertFalse(CheckTestOnlyStatus(repository)())

        assertFalse(repository.adminActive)
        assertTrue(repository.pending)
        assertTrue(repository.savedStatuses.isEmpty())
    }

    @Test
    fun failedRemoval_doesNotActivateOrCompleteMigration() = runTest {
        val repository = FakeMainActivityRepository(true, false, true).apply {
            removalFails = true
        }

        assertFalse(CheckTestOnlyStatus(repository)())

        assertTrue(repository.adminActive)
        assertTrue(repository.pending)
        assertEquals(0, repository.activationCalls)
        assertTrue(repository.savedStatuses.isEmpty())
    }

    @Test
    fun cancellationDuringActivation_propagatesAndPreservesRecoveryIntent() = runTest {
        val repository = FakeMainActivityRepository(true, false, true).apply {
            activationCancellation = CancellationException("Process stopping")
        }

        try {
            CheckTestOnlyStatus(repository)()
            fail("Cancellation must propagate")
        } catch (_: CancellationException) {
            assertTrue(repository.pending)
            assertTrue(repository.savedStatuses.isEmpty())
        }
    }

    @Test
    fun cancellingRetryLoop_preventsFurtherAttempts() = runTest {
        val repository = FakeMainActivityRepository(true, false, true).apply {
            backendAvailable = false
        }
        val job = launch { CheckTestOnlyStatus(repository).runUntilComplete() }
        runCurrent()

        job.cancelAndJoin()
        advanceTimeBy(30_000)
        runCurrent()

        assertEquals(listOf("preflight"), repository.events)
        assertTrue(repository.adminActive)
    }

    private class FakeMainActivityRepository(
        private val currentStatus: Boolean,
        private var savedStatus: Boolean?,
        private val appUpdated: Boolean,
        var adminRecreationSucceeds: Boolean = true
    ) : MainActivityRepository {
        var backendChecks = 0
            private set
        val savedStatuses = mutableListOf<Boolean>()
        val events = mutableListOf<String>()
        var userUnlocked = true
        var adminActive = true
        var pending = false
        var backendAvailable = true
        var removalFails = false
        var activationTakesEffect = true
        var activationCancellation: CancellationException? = null
        var removalCalls = 0
        var activationCalls = 0

        override val uiSettings: Flow<UISettings> = emptyFlow()
        override val appLatestData: Flow<RequestResult<AppLatestVersion>> = emptyFlow()
        override val displayUpdateNotification: Flow<Boolean> = emptyFlow()
        override val testOnlyNeeded: Flow<Boolean> = emptyFlow()

        override suspend fun checkUpdates() = Unit

        override fun isUserUnlocked(): Boolean = userUnlocked
        override fun hasAdminRights(): Boolean = adminActive
        override suspend fun isAdminReactivationPending(): Boolean = pending

        override suspend fun saveAdminReactivationPending(pending: Boolean) {
            events += "pending:$pending"
            this.pending = pending
        }

        override suspend fun removeAdminRights() {
            events += "remove"
            removalCalls++
            if (removalFails) throw IllegalStateException("Removal failed")
            adminActive = false
        }

        override suspend fun getDeviceAdminActivator(): DeviceAdminActivator? {
            events += "preflight"
            backendChecks++
            if (!backendAvailable) return null
            return DeviceAdminActivator {
                events += "activate"
                activationCalls++
                activationCancellation?.let { throw it }
                if (!adminRecreationSucceeds) throw IllegalStateException("Shizuku disconnected")
                adminActive = activationTakesEffect
            }
        }

        override suspend fun savedTestOnlyStatus(): Boolean? = savedStatus

        override suspend fun saveTestOnlyStatus(status: Boolean) {
            savedStatuses += status
            savedStatus = status
        }

        override fun isTestOnly(): Boolean = currentStatus

        override fun isAppUpdated(): Boolean = appUpdated
    }
}
