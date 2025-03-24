package com.sonozaki.files.domain.usecases

import com.sonozaki.files.domain.repository.FilesScreenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilesDeletionEnabledUseCase @Inject constructor(private val repository: FilesScreenRepository) {
    operator fun invoke(): Flow<Boolean> = repository.deletionEnabled
}