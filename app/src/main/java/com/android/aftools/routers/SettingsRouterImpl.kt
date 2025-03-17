package com.android.aftools.routers

import android.content.Context
import com.sonozaki.services.services.MyJobIntentService
import com.sonozaki.settings.domain.routers.SettingsRouter

class SettingsRouterImpl: SettingsRouter {
    override fun startService(context: Context) {
        MyJobIntentService.start(context)
    }
}