package com.android.aftools.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class UsbSettingsV1(val runOnConnection: Boolean=false, )