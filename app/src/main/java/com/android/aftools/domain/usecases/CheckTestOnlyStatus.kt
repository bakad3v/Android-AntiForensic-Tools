package com.android.aftools.domain.usecases

import com.android.aftools.domain.repository.MainActivityRepository
import javax.inject.Inject

class CheckTestOnlyStatus @Inject constructor(
    private val repository: MainActivityRepository
) {
    suspend operator fun invoke() {
        val currentStatus = repository.isTestOnly()
        val savedStatus = repository.savedTestOnlyStatus()

        when {
            !currentStatus -> repository.saveTestOnlyStatus(false)
            savedStatus == true -> Unit
            savedStatus == false -> removeAdminAndSaveStatus()
            repository.isAppUpdated() -> removeAdminAndSaveStatus()
            else -> repository.saveTestOnlyStatus(true)
        }
    }

    private suspend fun removeAdminAndSaveStatus() {
        if (repository.disableAdmin()) {
            repository.saveTestOnlyStatus(true)
        }
    }
}
