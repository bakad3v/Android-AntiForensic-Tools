package com.sonozaki.settings.domain.usecases.permissions

import com.sonozaki.entities.Permissions
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPermissionsUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
    operator fun invoke(): Flow<Permissions> {
        return repository.permissions
    }
}