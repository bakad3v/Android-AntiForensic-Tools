package com.android.aftools.domain.usecases

import com.android.aftools.domain.repository.MainActivityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class CheckTestOnlyStatus @Inject constructor(
    private val repository: MainActivityRepository
) {
    private val mutex = Mutex()

    /** Retry in this process as well as after a restart; stored permissions can outlive a backend. */
    suspend fun runUntilComplete() {
        while (!invoke()) {
            delay(RETRY_DELAY_MS)
        }
    }

    suspend operator fun invoke(): Boolean = mutex.withLock {
        val currentStatus = repository.isTestOnly()
        val savedStatus = repository.savedTestOnlyStatus()

        when {
            !currentStatus -> {
                repository.saveTestOnlyStatus(false)
                true
            }
            savedStatus == true -> true
            savedStatus == false || repository.isAppUpdated() -> recreateAdminAndSaveStatus()
            else -> {
                repository.saveTestOnlyStatus(true)
                true
            }
        }
    }

    private suspend fun recreateAdminAndSaveStatus(): Boolean {
        if (!repository.isUserUnlocked()) return false

        return try {
            val pending = repository.isAdminReactivationPending()
            val active = repository.hasAdminRights()
            if (pending || active) {
                // Resolve a live backend before removal, and keep using it even if permission
                // settings change during the migration. The backend can still die afterwards,
                // so persist recovery intent first and retry activation on subsequent attempts.
                val activator = repository.getDeviceAdminActivator() ?: return false
                if (active) {
                    if (!pending) repository.saveAdminReactivationPending(true)
                    repository.removeAdminRights()
                    if (repository.hasAdminRights()) return false
                }
                activator.activate()
                if (!repository.hasAdminRights()) return false
                repository.saveAdminReactivationPending(false)
            }
            repository.saveTestOnlyStatus(true)
            true
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            false
        }
    }

    companion object {
        private const val RETRY_DELAY_MS = 5_000L
    }
}
