package com.sonozaki.triggerreceivers.services.domain.router

import android.content.Context

interface ActivitiesLauncher {
    fun launchService(context: Context)
    suspend fun startBFU()
    suspend fun startAFU(): Boolean
    fun enqueueReboot(delay: Int)
    fun stopReboot()
}
