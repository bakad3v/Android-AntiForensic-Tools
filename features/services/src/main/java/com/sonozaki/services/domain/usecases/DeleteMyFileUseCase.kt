package com.sonozaki.services.domain.usecases

import android.net.Uri
import com.sonozaki.services.domain.repository.ServicesRepository
import javax.inject.Inject

class DeleteMyFileUseCase @Inject constructor(private val repository: ServicesRepository) {
    suspend operator fun invoke(uri: Uri) {
        repository.deleteMyFile(uri)
    }
}