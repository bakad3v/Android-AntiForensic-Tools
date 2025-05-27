package com.sonozaki.settings.domain.usecases.appUpdate

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUpdatePopupStatusUseCase @Inject constructor(
    private val repository: SettingsScreenRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.showUpdatePopup
    }
}