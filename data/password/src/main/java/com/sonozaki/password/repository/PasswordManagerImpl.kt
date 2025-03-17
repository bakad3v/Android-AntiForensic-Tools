package com.sonozaki.password.repository

import android.content.Context
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.encrypteddatastore.datastoreDBA.dataStoreDirectBootAware
import com.sonozaki.password.entities.PasswordStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class PasswordManagerImpl @Inject constructor(
  @ApplicationContext private val context: Context,
  passwordStatusSerializer: EncryptedSerializer<PasswordStatus>
) : PasswordManager {
  private val Context.passwordPrefs by dataStoreDirectBootAware(
      PREFERENCES_NAME,
      passwordStatusSerializer
  )


  override val passwordStatus = context.passwordPrefs.data.map { preferences ->
    preferences.passwordSet
  }



  override suspend fun setPassword(password: CharArray) {
    context.passwordPrefs.updateData {
      PasswordStatus(password = password.concatToString(), passwordSet = true)
    }
    password.clear()
  }


  override suspend fun checkPassword(password: CharArray): Boolean {
    val currentPassword = context.passwordPrefs.data.first().password
    val rightPassword = password.concatToString() == currentPassword
    password.clear()
    return rightPassword
  }

  private fun CharArray.clear() {
    for (i in indices) {
      this[i] = '\u0000'
    }
  }

  companion object {
    private const val PREFERENCES_NAME = "password_preferences"
  }
}
