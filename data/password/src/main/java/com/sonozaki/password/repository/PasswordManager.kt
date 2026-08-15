package com.sonozaki.password.repository

import kotlinx.coroutines.flow.Flow

/** Repository for storing and verifying a salted password hash. */
interface PasswordManager {
  suspend fun setPassword(password: CharArray)
  suspend fun checkPassword(password: CharArray): Boolean

  /**
   * Is password set?
   */
  val passwordStatus: Flow<Boolean>
}
