package com.android.aftools.domain.entities

import android.graphics.drawable.Drawable

data class AppDomain(val packageName: String, val appName: String, val system: Boolean,val enabled: Boolean, val toDelete: Boolean = false, val toHide: Boolean = false, val toClearData: Boolean = false,val icon: Drawable)