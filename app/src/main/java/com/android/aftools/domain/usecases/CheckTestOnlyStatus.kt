package com.android.aftools.domain.usecases

import com.android.aftools.domain.repository.MainActivityRepository
import javax.inject.Inject

class CheckTestOnlyStatus @Inject constructor(
    private val repository: MainActivityRepository
) {
    suspend operator fun invoke() {
        val isTestOnlyCurrent = repository.isTestOnly()
        if (!repository.savedTestOnlyStatus() && isTestOnlyCurrent) {
            repository.disableAdmin()
        }
        repository.saveTestOnlyStatus(isTestOnlyCurrent)
    }
}