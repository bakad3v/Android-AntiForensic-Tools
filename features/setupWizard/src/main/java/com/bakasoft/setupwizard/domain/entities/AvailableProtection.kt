package com.bakasoft.setupwizard.domain.entities

data class AvailableProtection(val uninstallItself: Boolean, val hideItself: Boolean,
     val disableSafeBoot: Boolean, val disableLogs: Boolean,
     val hideMultiuserUI: Boolean, val activateTrim: Boolean)