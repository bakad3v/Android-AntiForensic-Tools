package com.oasisfeng.island.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class PasswordStatus(val password: String = "", val passwordSet: Boolean=false) {
}
