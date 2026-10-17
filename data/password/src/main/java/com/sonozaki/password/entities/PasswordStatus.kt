package com.sonozaki.password.entities

import kotlinx.serialization.Serializable

@Serializable
data class PasswordStatus(
    val passwordHash: PasswordHash? = null,
    val passwordSet: Boolean = false
)

@Serializable
data class PasswordHash(
    val hash: String,
    val salt: String,
    val algorithm: String,
    val algorithmVersion: Int,
    val memoryCostKiB: Int,
    val iterations: Int,
    val parallelism: Int
)
