package com.android.aftools.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class UISettings(
    val allowScreenshots: Boolean = false,
    val theme: Theme = Theme.SYSTEM_THEME)