package com.sonozaki.password.entities

import kotlinx.serialization.Serializable

/** Plaintext password format used by app versions before password hashing was introduced. */
@Serializable
data class PasswordStatusV1(
    val password: String = "",
    val passwordSet: Boolean = false
)
