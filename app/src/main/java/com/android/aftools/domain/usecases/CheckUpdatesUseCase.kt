package com.android.aftools.domain.usecases

import com.android.aftools.domain.entities.UpdateCheckResult
import com.android.aftools.domain.repository.MainActivityRepository
import com.bakasoft.network.RequestResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckUpdatesUseCase @Inject constructor(
    private val repository: MainActivityRepository
)
{
    suspend operator fun invoke(): UpdateCheckResult {
        if (!repository.displayUpdateNotification.first()) {
            return UpdateCheckResult.CHECK_NOTHING
        }
        repository.checkUpdates()
        val data = repository.appLatestData.first()
        return when (data) {
            is RequestResult.Error -> UpdateCheckResult.CHECK_ERROR
            is RequestResult.Data -> {
                if (data.data.newVersion || !data.data.isTestOnly) {
                    UpdateCheckResult.CHECK_UPDATE
                } else {
                    UpdateCheckResult.CHECK_NOTHING
                }
            }
        }
    }
}