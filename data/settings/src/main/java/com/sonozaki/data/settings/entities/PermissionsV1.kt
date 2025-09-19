package com.sonozaki.data.settings.entities

import kotlinx.serialization.Serializable

@Serializable
data class PermissionsV1(
    val isAdmin: Boolean=false,
    val isOwner: Boolean=false,
    val isRoot: Boolean=false
)