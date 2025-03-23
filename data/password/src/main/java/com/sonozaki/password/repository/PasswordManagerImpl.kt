package com.sonozaki.password.repository

import android.content.Context
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.password.entities.PasswordStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class PasswordManagerImpl @Inject constructor(
  @ApplicationContext private val context: Context,
  passwordStatusSerializer: BaseSerializer<PasswordStatus>
) : PasswordManager {
  private val Context.passwordPrefs by encryptedDataStore(
      PREFERENCES_NAME,
      passwordStatusSerializer,
    alias = EncryptionAlias.PASSWORD.name,
    isDBA = true
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
