package com.sonozaki.triggerreceivers.services

import android.accessibilityservice.AccessibilityService
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.UserManager
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_VOLUME_DOWN
import android.view.KeyEvent.KEYCODE_VOLUME_UP
import android.view.accessibility.AccessibilityEvent
import com.sonozaki.entities.ButtonClicked
import com.sonozaki.entities.MultiuserUIProtection
import com.sonozaki.entities.UsbSettings
import com.sonozaki.resources.IO_DISPATCHER
import com.sonozaki.superuser.superuser.SuperUser
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.superuser.superuser.SuperUserManager
import com.sonozaki.triggerreceivers.R
import com.sonozaki.triggerreceivers.services.domain.router.ActivitiesLauncher
import com.sonozaki.triggerreceivers.services.domain.usecases.ButtonClickUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.CheckPasswordUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetButtonsRootDataUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetDeviceProtectionSettings
import com.sonozaki.triggerreceivers.services.domain.usecases.GetLogsEnabledUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetPasswordStatusUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetPermissionsUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetSettingsUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetUsbSettingsUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.SetRunOnBootUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.SetServiceStatusUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.WriteLogsUseCase
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
    @Named(IO_DISPATCHER)
    lateinit var dispatcher: CoroutineDispatcher

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var getPasswordStatusUseCase: GetPasswordStatusUseCase

    @Inject
    lateinit var buttonClicksUseCase: ButtonClickUseCase

    @Inject
    lateinit var getDeviceProtectionSettings: GetDeviceProtectionSettings

    @Inject
    lateinit var getLogsEnabledUseCase: GetLogsEnabledUseCase

    @Inject
    lateinit var writeLogsUseCase: WriteLogsUseCase

    @Inject
    lateinit var getPermissionsUseCase: GetPermissionsUseCase

    @Inject
    lateinit var getButtonsRootDataUseCase: GetButtonsRootDataUseCase

    override fun onCreate() {
        super.onCreate()
        Log.w("powerButtonClicks", "create")
        coroutineScope.launch(dispatcher) {
            var cancelCallback: (() -> Unit)? = null
            getButtonsRootDataUseCase().collect {
                if (it) {
                    cancelCallback = listenForButtonClicksRoot(superUserManager.getSuperUser())
                } else {
                    cancelCallback?.invoke()
                }
            }
        }
        Log.w("powerButtonClicks", "skip")
        coroutineScope.launch(dispatcher) {
            if (!getPasswordStatusUseCase()) {
                //app will not set service status to true if it's data has been reset.
                stopSelf()
                return@launch
            }
            setServiceStatusUseCase(true)
            protectMultiuserUI()
            setLogdServiceStatus()
        }
        listenScreenStateChange()
        listenScreenUnlocked()
        listenUserUnlocked()
        listenUsbConnection()
        keyguardManager = getSystemService(KeyguardManager::class.java)
    }

    private fun listenForButtonClicksRoot(superUser: SuperUser): () -> Unit {
        return superUser.getPowerButtonClicks {
            if (!it) return@getPowerButtonClicks
            coroutineScope.launch(dispatcher) {
                if (buttonClicksUseCase(ButtonClicked.POWER_BUTTON)) {
                    runActions()
                }
            }
        }
    }

    private suspend fun writeLogs(text: String) {
        if (getLogsEnabledUseCase()) {
            writeLogsUseCase(text)
        }
    }


    private fun listenScreenUnlocked() {
        val screenUnlockedFilter = IntentFilter(Intent.ACTION_USER_PRESENT)
        val screenUnlockedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                activitiesLauncher.stopReboot()
            }
        }
        registerReceiver(screenUnlockedReceiver, screenUnlockedFilter)
    }

    private suspend fun disableMultiuserUI() {
        val permissions = getPermissionsUseCase()
        try {
            writeLogs(baseContext.getString(R.string.disabling_multiuser_ui))
            if (permissions.isRoot && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                superUserManager.getSuperUser().setUserSwitcherStatus(false)
            } else {
                superUserManager.getSuperUser().setSwitchUserRestriction(true)
            }
        } catch (e: SuperUserException) {
            writeLogs(
                baseContext.getString(
                    R.string.disabling_muliuser_ui_failed,
                    e.messageForLogs.asString(baseContext)
                )
            )
        }
    }

    private suspend fun protectMultiuserUI() {
        if (getDeviceProtectionSettings().multiuserUIProtection == MultiuserUIProtection.ON_REBOOT) {
            disableMultiuserUI()
        }
    }

    private suspend fun handleScreenStateChanged(action: String) {
        val root = getPermissionsUseCase().isRoot
        if (!root && buttonClicksUseCase(ButtonClicked.POWER_BUTTON)) {
            runActions()
        }
        if (action == Intent.ACTION_SCREEN_OFF) {
            val deviceProtectionSettings = getDeviceProtectionSettings()
            if (deviceProtectionSettings.rebootOnLock && userManager.isUserUnlocked) {
                activitiesLauncher.enqueueReboot(deviceProtectionSettings.rebootDelay)
            }
            if (deviceProtectionSettings.multiuserUIProtection == MultiuserUIProtection.ON_SCREEN_OFF) {
                disableMultiuserUI()
            }
        }
    }

    private fun listenScreenStateChange() {
        val screenStateChangedFilter =
            IntentFilter(Intent.ACTION_SCREEN_ON).apply { addAction(Intent.ACTION_SCREEN_OFF) }
        val buttonClickedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                coroutineScope.launch(dispatcher) {
                    handleScreenStateChanged(intent?.action ?: "")
                }
            }
        }
        registerReceiver(buttonClickedReceiver, screenStateChangedFilter)
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
                    coroutineScope.launch(dispatcher) {
                        runOnUSBConnected()
                    }
                    return
                }
                val manager = getSystemService(USB_SERVICE) as UsbManager
                if (intent?.extras?.getBoolean("connected") == true && (manager.deviceList?.size != 0 || manager.accessoryList?.size != 0)) {
                    coroutineScope.launch(dispatcher) {
                        runOnUSBConnected()
                    }
                }
            }
        }
        registerReceiver(usbReceiver, usbFilter)
    }

    private suspend fun runOnUSBConnected() {
        val settings = getUsbSettingsUseCase()
        when (settings) {
            UsbSettings.RUN_ON_CONNECTION -> runActions()
            UsbSettings.REBOOT_ON_CONNECTION -> try {
                writeLogs(baseContext.getString(R.string.rebooting))
                superUserManager.getSuperUser().reboot()
            } catch (e: SuperUserException) {
                writeLogs(
                    baseContext.getString(
                        R.string.rebooting_failed,
                        e.messageForLogs.asString(baseContext)
                    )
                )
            }

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
                coroutineScope.launch(dispatcher) {
                    if (getSettingsUseCase().runOnBoot) {
                        withContext(dispatcher) {
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
                writeLogs(baseContext.getString(R.string.stopping_logs))
                superUserManager.getSuperUser().stopLogd()
            } catch (e: SuperUserException) {
                writeLogs(
                    baseContext.getString(
                        R.string.stopping_logs_failed,
                        e.messageForLogs.asString(baseContext)
                    )
                )
            }
        }
    }

    private fun checkPassword(pass: CharArray) {
        coroutineScope.launch(dispatcher) {
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
        withContext(dispatcher) {
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
            } catch (_: java.lang.IndexOutOfBoundsException) {
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.packageName !in PACKAGE_NAMES_INTERCEPTED || keyguardManager?.isDeviceLocked != true ||
            event.isEnabled != true || event.isPassword !=true
        ) return
        updatePassword(event.text.joinToString(""))
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == ACTION_DOWN) {
            val buttonClicked = if (event.keyCode == KEYCODE_VOLUME_UP) {
                ButtonClicked.VOLUME_UP
            } else if (event.keyCode == KEYCODE_VOLUME_DOWN) {
                ButtonClicked.VOLUME_DOWN
            } else {
                return super.onKeyEvent(event)
            }
            coroutineScope.launch(dispatcher) {
                if (buttonClicksUseCase(buttonClicked)) {
                    runActions()
                }
            }
        }
        return super.onKeyEvent(event)
    }

    override fun onInterrupt() {
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
        private val PACKAGE_NAMES_INTERCEPTED = setOf("com.android.systemui","com.android.keyguard")
    }
}
