package com.bakasoft.setupwizard.domain.usecases

import com.bakasoft.setupwizard.domain.entities.AvailableProtection
import com.bakasoft.setupwizard.domain.repository.SetupWizardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetAvailableProtectionUseCase @Inject constructor(
    private val repository: SetupWizardRepository
) {
    operator fun invoke(): Flow<AvailableProtection> {
        return combine(repository.settings, repository.permissions) {
            settings, permissions ->
            val rootOrShizuku = permissions.isRoot || permissions.isShizuku
            AvailableProtection(
                uninstallItself = rootOrShizuku,
                hideItself = permissions.isOwner,
                disableSafeBoot = permissions.isRoot || permissions.isShizuku || permissions.isOwner,
                disableLogs = rootOrShizuku,
                hideMultiuserUI = settings.serviceWorking && (permissions.isOwner || permissions.isRoot),
                activateTrim = rootOrShizuku
            )
        }
    }
}