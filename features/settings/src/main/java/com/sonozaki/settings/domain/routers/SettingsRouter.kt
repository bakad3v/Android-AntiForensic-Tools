package com.sonozaki.settings.domain.routers

import android.content.Context

interface SettingsRouter {
    fun startService(context: Context)
}