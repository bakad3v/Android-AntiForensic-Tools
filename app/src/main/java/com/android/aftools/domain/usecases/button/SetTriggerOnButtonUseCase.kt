package com.android.aftools.domain.usecases.button

import com.android.aftools.domain.repositories.ButtonSettingsRepository
import javax.inject.Inject

class SetTriggerOnButtonUseCase @Inject constructor(val repository: ButtonSettingsRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setTriggerOnButtonStatus(status)
    }
}