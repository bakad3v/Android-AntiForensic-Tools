package com.bakasoft.appupdatecenter.domain

interface AppUpdateRouter {
    fun launchUpdate()
    fun killNotifications()
}