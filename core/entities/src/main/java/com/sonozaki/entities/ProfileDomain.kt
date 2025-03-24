package com.sonozaki.entities

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDomain(val id: Int, val name: String, val main: Boolean, val toDelete: Boolean=false)