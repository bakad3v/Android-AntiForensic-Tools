package com.sonozaki.triggerreceivers.services.domain.usecases

import android.util.Log
import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class CheckPasswordUseCase @Inject constructor(private val repository: ReceiversRepository) {
  suspend operator fun invoke(password: CharArray): Boolean {
    Log.w("checkingPassword", password.joinToString(""))
    val result = repository.checkPassword(password)
    Log.w("checkingPasswordEnd", password.joinToString(""))
    return result
  }
}
