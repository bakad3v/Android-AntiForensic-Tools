package com.android.aftools.routers

import android.content.Context
import com.sonozaki.services.services.MyJobIntentService
import com.sonozaki.settings.domain.routers.SettingsRouter
import javax.inject.Inject

class SettingsRouterImpl @Inject constructor(): SettingsRouter {
    override fun startService(context: Context) {
        MyJobIntentService.start(context)
    }
}