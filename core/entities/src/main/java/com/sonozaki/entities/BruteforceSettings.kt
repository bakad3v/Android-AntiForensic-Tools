package com.sonozaki.entities

import kotlinx.serialization.Serializable

@Serializable
data class BruteforceSettings(val bruteforceRestricted: Boolean=false, val wrongAttempts: Int=0, val allowedAttempts: Int=10)