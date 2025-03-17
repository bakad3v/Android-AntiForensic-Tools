package com.sonozaki.triggerreceivers.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.UserManager
import android.view.accessibility.AccessibilityEvent
import com.sonozaki.entities.UsbSettings
import com.sonozaki.resources.IO_DISPATCHER
import com.sonozaki.superuser.superuser.SuperUserManager
import com.sonozaki.triggerreceivers.services.domain.router.ActivitiesLauncher
import com.sonozaki.triggerreceivers.services.domain.usecases.ButtonClickUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.CheckPasswordUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetPasswordStatusUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetSettingsUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetUsbSettingsUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.SetRunOnBootUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.SetServiceStatusUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

/**
 * Accessibility service for password interception and usb connections monitoring. Thanks x13a for idea.
 */
@AndroidEntryPoint
class TriggerReceiverService : AccessibilityService() {
    private var keyguardManager: KeyguardManager? = null
    private var password = mutableListOf<Char>()

    @Inject
    lateinit var checkPasswordUseCase: CheckPasswordUseCase

    @Inject
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var setServiceStatusUseCase: SetServiceStatusUseCase

    @Inject
    lateinit var setRunOnBootUseCase: SetRunOnBootUseCase

    @Inject
    lateinit var getSettingsUseCase: GetSettingsUseCase

    @Inject
    lateinit var activitiesLauncher: ActivitiesLauncher

    @Inject
    lateinit var superUserManager: SuperUserManager

    @Inject
    lateinit var getUsbSettingsUseCase: GetUsbSettingsUseCase

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var getPasswordStatusUseCase: GetPasswordStatusUseCase

    @Inject
    lateinit var buttonClicksUseCase: ButtonClickUseCase

    @Inject
    @Named(IO_DISPATCHER)
    lateinit var ioDispatcher: CoroutineDispatcher

    override fun onCreate() {
        super.onCreate()
        coroutineScope.launch {
            if (!getPasswordStatusUseCase()) {
               //app will not set service status to true if it's data has been reset.
               stopSelf()
               return@launch
            }
            setServiceStatusUseCase(true)
            setLogdServiceStatus()
        }
        listenButtonClicked()
        listenUserUnlocked()
        listenUsbConnection()
        keyguardManager = getSystemService(KeyguardManager::class.java)
    }

    private fun listenButtonClicked() {
        val screenStateChangedFilter =
            IntentFilter(Intent.ACTION_SCREEN_ON).apply { addAction(Intent.ACTION_SCREEN_OFF) }
        val buttonClickedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                coroutineScope.launch {
                    if (buttonClicksUseCase()) {
                        runActions()
                    }
                }
            }
        }
        registerReceiver(buttonClickedReceiver,screenStateChangedFilter)
    }

    /**
     * Listen to usb connection events and react if needed.
     */
    private fun listenUsbConnection() {
        val usbFilter =
            IntentFilter("android.hardware.usb.action.USB_STATE").apply { addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED) }
                .apply { addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED) }
        val usbReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == UsbManager.ACTION_USB_ACCESSORY_ATTACHED || intent?.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
                    coroutineScope.launch {
                        runOnUSBConnected()
                    }
                    return
                }
                val manager = getSystemService(USB_SERVICE) as UsbManager
                if (intent?.extras?.getBoolean("connected") == true && (manager.deviceList?.size != 0 || manager.accessoryList?.size != 0)) {
                    coroutineScope.launch {
                        runOnUSBConnected()
                    }
                }
            }
        }
        registerReceiver(usbReceiver, usbFilter)
    }

    private suspend fun runOnUSBConnected() {
        val settings = getUsbSettingsUseCase()
        when(settings) {
            UsbSettings.RUN_ON_CONNECTION -> runActions()
            UsbSettings.REBOOT_ON_CONNECTION -> try {
                superUserManager.getSuperUser().reboot()
            } catch (_: Exception) {}
            UsbSettings.DO_NOTHING -> {}
        }
    }

    /**
     * If user unlocked and some actions were postponed until device unlocking, run these actions
     */
    private fun listenUserUnlocked() {
        val userUnlockedFilter = IntentFilter(Intent.ACTION_USER_UNLOCKED)
        val userUnlockedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                coroutineScope.launch {
                    if (getSettingsUseCase().runOnBoot) {
                        withContext(ioDispatcher) {
                            delay(2000) //for some reason, file deletion doesn't work without delay after unlocking
                            activitiesLauncher.startAFU()
                        }
                        setRunOnBootUseCase(false)
                    }
                }
            }
        }
        registerReceiver(userUnlockedReceiver, userUnlockedFilter)
    }

    /**
     * Stop logd service on accessibility service start
     */
    private suspend fun setLogdServiceStatus() {
        val settings = getSettingsUseCase()
        if (settings.stopLogdOnBoot) {
            try {
                superUserManager.getSuperUser().stopLogd()
            } catch (_: Exception) {

            }
        }
    }

    private fun checkPassword(pass: CharArray) {
        coroutineScope.launch {
            if (getSettingsUseCase().runOnDuressPassword && checkPasswordUseCase(pass)) {
                runActions()
            }
            password = mutableListOf()
        }
    }

    /**
     * Run in background thread actions that can be started before the device is unlocked. If the device remains locked, it postpones other actions until unlocked, otherwise it performs them immediately.
     */
    private suspend fun runActions() {
        withContext(ioDispatcher) {
            activitiesLauncher.startBFU()
            if (userManager.isUserUnlocked) {
                activitiesLauncher.startAFU()
            } else
                setRunOnBootUseCase(true)
        }
    }

    private fun updatePassword(text: String) {
        val ignoreChars = text.count { it == IGNORE_CHAR }
        if (ignoreChars == 0 && text.length != 1) {
            checkPassword(password.toCharArray())
            return
        }
        if (password.size > text.length) {
            password = password.subList(0, text.length)
        }
        if (ignoreChars == text.length)
            return
        val index = text.indexOfFirst { it != IGNORE_CHAR }
        if (index == password.size) {
            password.add(text[index])
        } else {
            try {
                password[index] = text[index]
            } catch (_: java.lang.IndexOutOfBoundsException) { }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (keyguardManager?.isDeviceLocked != true ||
            event?.isEnabled != true
        ) return
        updatePassword(event.text.joinToString(""))
    }

    override fun onInterrupt() {
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        runBlocking {
           setServiceStatusUseCase(false)
        }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val IGNORE_CHAR = '•'
    }
}
