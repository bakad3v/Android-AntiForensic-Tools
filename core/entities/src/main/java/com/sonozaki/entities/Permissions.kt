package com.sonozaki.entities

import kotlinx.serialization.Serializable

@Serializable
data class Permissions(
    val isAdmin: Boolean = false,
    val isOwner: Boolean = false,
    val isRoot: Boolean = false,
    val isShizuku: Boolean = false)