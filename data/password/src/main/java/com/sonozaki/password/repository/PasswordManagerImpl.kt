package com.sonozaki.password.repository

import android.content.Context
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.password.dataMigration.PasswordHashMigration
import com.sonozaki.password.entities.PasswordStatus
import com.sonozaki.password.security.PasswordHasher
import com.sonozaki.resources.DEFAULT_DISPATCHER
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named


class PasswordManagerImpl @Inject constructor(
  @ApplicationContext private val context: Context,
  passwordStatusSerializer: BaseSerializer<PasswordStatus>,
  passwordHashMigration: PasswordHashMigration,
  private val passwordHasher: PasswordHasher,
  private val passwordOperationGate: PasswordOperationGate,
  @Named(DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher
) : PasswordManager {
  private val Context.passwordPrefs by encryptedDataStore(
    PREFERENCES_NAME,
    passwordStatusSerializer,
    produceMigrations = {
      listOf(passwordHashMigration)
    },
    alias = EncryptionAlias.PASSWORD.name,
    isDBA = true
  )

  override val passwordStatus = context.passwordPrefs.data.map { preferences ->
    preferences.passwordSet && preferences.passwordHash != null
  }

  override suspend fun setPassword(password: CharArray) {
    try {
      passwordOperationGate.runExclusive {
        val passwordHash = withContext(defaultDispatcher) {
          passwordHasher.hash(password)
        }
        context.passwordPrefs.updateData {
          PasswordStatus(passwordHash = passwordHash, passwordSet = true)
        }
      }
    } finally {
      password.clear()
    }
  }

  override suspend fun checkPassword(password: CharArray): Boolean {
    return try {
      passwordOperationGate.runExclusive {
        val passwordHash = context.passwordPrefs.data.first().passwordHash
        passwordHash != null && withContext(defaultDispatcher) {
          passwordHasher.verify(password, passwordHash)
        }
      }
    } finally {
      password.clear()
    }
  }

  private fun CharArray.clear() {
    fill('\u0000')
  }

  companion object {
    private const val PREFERENCES_NAME = "password_preferences_v2"
  }
}
