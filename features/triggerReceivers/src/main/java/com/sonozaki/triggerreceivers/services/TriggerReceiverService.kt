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
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_VOLUME_DOWN
import android.view.KeyEvent.KEYCODE_VOLUME_UP
import android.view.accessibility.AccessibilityEvent
import com.sonozaki.entities.BruteforceDetectingMethod
import com.sonozaki.entities.ButtonClicked
import com.sonozaki.entities.MultiuserUIProtection
import com.sonozaki.entities.PowerButtonTriggerOptions
import com.sonozaki.entities.UsbSettings
import com.sonozaki.resources.IO_DISPATCHER
import com.sonozaki.superuser.superuser.SuperUser
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.superuser.superuser.SuperUserManager
import com.sonozaki.triggerreceivers.R
import com.sonozaki.triggerreceivers.services.domain.router.ActivitiesLauncher
import com.sonozaki.triggerreceivers.services.domain.usecases.ButtonClickUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.CheckPasswordUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetBruteforceSettingsUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetButtonSettingsUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetButtonsRootDataUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetDeviceProtectionSettings
import com.sonozaki.triggerreceivers.services.domain.usecases.GetLogsEnabledUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetPasswordStatusUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetPermissionsUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetSettingsUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.GetUsbSettingsUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.OnRightPasswordUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.OnWrongPasswordUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.SetRunOnBootUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.SetServiceStatusUseCase
import com.sonozaki.triggerreceivers.services.domain.usecases.WriteLogsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.milliseconds

/**
 * Accessibility service for password interception and usb connections monitoring. Thanks x13a for idea.
 */
@AndroidEntryPoint
class TriggerReceiverService : AccessibilityService() {
    private var keyguardManager: KeyguardManager? = null
    private var usbConnected = false
    private var usbDataConnected = false
    private val pendingAfuMutex = Mutex()

    @Inject
    lateinit var passwordBuffer: LockScreenPasswordBuffer

    @Inject
    lateinit var checkPasswordUseCase: CheckPasswordUseCase

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> }

    private val coroutineScope by lazy {
        CoroutineScope(SupervisorJob() + dispatcher + coroutineExceptionHandler)
    }

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
    lateinit var getButtonSettingsUseCase: GetButtonSettingsUseCase

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

    @Inject
    lateinit var getBruteforceSettingsUseCase: GetBruteforceSettingsUseCase

    @Inject
    lateinit var onWrongPasswordUseCase: OnWrongPasswordUseCase

    @Inject
    lateinit var onRightPasswordUseCase: OnRightPasswordUseCase

    override fun onCreate() {
        super.onCreate()
        coroutineScope.launch(dispatcher) {
            var cancelCallback: (() -> Unit)? = null
            try {
                getButtonsRootDataUseCase()
                    .distinctUntilChanged()
                    .collect { shouldListen ->
                        val previousCancelCallback = cancelCallback
                        cancelCallback = null
                        stopPowerButtonListener(previousCancelCallback)

                        if (shouldListen) {
                            cancelCallback = listenForButtonClicksRoot(superUserManager.getSuperUser())
                        }
                    }
            } finally {
                val finalCancelCallback = cancelCallback
                cancelCallback = null
                stopPowerButtonListener(finalCancelCallback)
            }
        }
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

    private fun stopPowerButtonListener(cancelCallback: (() -> Unit)?) {
        if (cancelCallback == null) {
            return
        }

        runCatching(cancelCallback)
    }

    /**
     * Listen for power button clicks
     * @return callback for stopping listening to power button clicks
     */
    private fun listenForButtonClicksRoot(superUser: SuperUser): () -> Unit {
        return superUser.getPowerButtonClicks {
            if (!it) return@getPowerButtonClicks
            coroutineScope.launch(dispatcher) {
                writeLogs(baseContext.getString(R.string.power_button_clicked))
                if (buttonClicksUseCase(ButtonClicked.POWER_BUTTON)) {
                    writeLogs(baseContext.getString(R.string.power_button_reason))
                    runActions()
                }
            }
        }
    }

    private suspend fun writeLogs(text: String) {
        try {
            if (getLogsEnabledUseCase()) {
                writeLogsUseCase(text)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // Logging is best-effort and must not prevent trigger processing.
        }
    }

    /**
     *  resets the counter of incorrect password attempts and prevents rebooting if user unlocked
     */
    private fun listenScreenUnlocked() {
        val screenUnlockedFilter = IntentFilter(Intent.ACTION_USER_PRESENT)
        val screenUnlockedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                coroutineScope.launch(dispatcher) {
                    if (checkBruteforceDetectionMethod()) {
                        onRightPasswordUseCase()
                    }
                }
                activitiesLauncher.stopReboot()
            }
        }
        registerReceiver(screenUnlockedReceiver, screenUnlockedFilter)
    }

    private suspend fun disableMultiuserUI() {
        val permissions = getPermissionsUseCase()
        try {
            writeLogs(baseContext.getString(R.string.disabling_multiuser_ui))
            if ((permissions.isRoot || permissions.isShizuku) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    superUserManager.getSuperUser().setUserSwitcherStatus(false)
                    superUserManager.getSuperUser().setSwitchUserRestriction(false)
                } catch (e: SuperUserException) {
                    superUserManager.getSuperUser().setSwitchUserRestriction(true)
                }
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

    /**
     * Disable multiuser UI on device boot if needed
     */
    private suspend fun protectMultiuserUI() {
        if (getDeviceProtectionSettings().multiuserUIProtection == MultiuserUIProtection.ON_REBOOT) {
            disableMultiuserUI()
        }
    }

    /**
     * When screen turns on|off, app can enqueue rebooting (to move device to BFU), hide multiuser UI
     * or use screen state changes as proxy for power button clicks (deprecated)
     */
    private suspend fun handleScreenStateChanged(action: String) {
        val deprecatedButton = getButtonSettingsUseCase().triggerOnButton == PowerButtonTriggerOptions.DEPRECATED_WAY
        if (deprecatedButton && buttonClicksUseCase(ButtonClicked.POWER_BUTTON)) {
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
        val usbFilter = IntentFilter(USB_STATE_ACTION).apply {
            addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
            addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        val usbReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) {
                    return
                }

                val manager = getSystemService(USB_SERVICE) as UsbManager
                val isConnected = when (intent.action) {
                    USB_STATE_ACTION -> {
                        usbDataConnected = intent.getBooleanExtra(USB_CONNECTED_EXTRA, false)
                        usbDataConnected || manager.deviceList.isNotEmpty() || !manager.accessoryList.isNullOrEmpty()
                    }
                    UsbManager.ACTION_USB_DEVICE_ATTACHED,
                    UsbManager.ACTION_USB_ACCESSORY_ATTACHED -> true
                    UsbManager.ACTION_USB_DEVICE_DETACHED,
                    UsbManager.ACTION_USB_ACCESSORY_DETACHED ->
                        usbDataConnected || manager.deviceList.isNotEmpty() || !manager.accessoryList.isNullOrEmpty()
                    else -> return
                }

                val isNewConnection = !usbConnected && isConnected
                usbConnected = isConnected
                if (isNewConnection) {
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
            UsbSettings.RUN_ON_CONNECTION -> {
                writeLogs(baseContext.getString(R.string.usb_reason))
                runActions()
            }
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
                    delay(AFU_DELAY_AFTER_UNLOCK_MS.milliseconds)
                    runPendingAfuIfUnlocked()
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
            try {
                if (getSettingsUseCase().runOnDuressPassword && checkPasswordUseCase(pass)) {
                    writeLogs(baseContext.getString(R.string.duress_password_reason))
                    runActions()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                writeLogs(baseContext.getString(R.string.password_verification_error))
            } finally {
                pass.fill('\u0000')
            }
        }
    }

    /**
     * Run in background thread actions that can be started before the device is unlocked. If the device remains locked, it postpones other actions until unlocked, otherwise it performs them immediately.
     */
    private suspend fun runActions() {
        withContext(dispatcher) {
            val wasUnlocked = userManager.isUserUnlocked
            activitiesLauncher.startBFU()
            // Persist the pending work before checking the unlock state. ACTION_USER_UNLOCKED can
            // now race only with runPendingAfuIfUnlocked(), which is serialized by its mutex.
            markAfuPending()
            if (userManager.isUserUnlocked) {
                if (!wasUnlocked) {
                    delay(AFU_DELAY_AFTER_UNLOCK_MS.milliseconds)
                }
                runPendingAfuIfUnlocked()
            }
        }
    }

    private suspend fun markAfuPending() {
        pendingAfuMutex.withLock {
            setRunOnBootUseCase(true)
        }
    }

    private suspend fun runPendingAfuIfUnlocked() {
        if (!userManager.isUserUnlocked) {
            return
        }

        pendingAfuMutex.withLock {
            if (!userManager.isUserUnlocked || !getSettingsUseCase().runOnBoot) {
                return
            }

            if (activitiesLauncher.startAFU()) {
                setRunOnBootUseCase(false)
            }
        }
    }

    /**
     * Trigger when password entered by user changes
     */
    private fun updatePassword(text: String) {
        passwordBuffer.update(text)?.let(::checkPassword)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.packageName !in PACKAGE_NAMES_INTERCEPTED || keyguardManager?.isDeviceLocked != true ||
            event.isEnabled != true
        ) return
        if (event.isPassword == true) {
            updatePassword(event.text.joinToString(""))
        } else {
            watchWrongPassword(event.text.joinToString(""))
        }
    }

    private suspend fun checkBruteforceDetectionMethod(): Boolean {
        return getBruteforceSettingsUseCase().detectingMethod == BruteforceDetectingMethod.ACCESSIBILITY_SERVICE
    }

    private fun compareStringWithResource(text: String, rId: Int): Boolean {
        return text.compareTo(baseContext.getString(rId), true) == 0
    }

    /**
     * Check if user entered wrong password without admin privileges. May not work on some devices
     */
    private fun watchWrongPassword(text: String) {
        coroutineScope.launch(dispatcher) {
            if (checkBruteforceDetectionMethod()
                && (compareStringWithResource(text, R.string.kg_wrong_password)
                || compareStringWithResource(text, R.string.wrong_password))
            ) {
                writeLogs(baseContext.getString(R.string.wrong_password_detected_accessibility))
                if (onWrongPasswordUseCase()) {
                    writeLogs(baseContext.getString(R.string.wrong_password_detected_accessibility_reason))

                    runActions()
                }
            }
        }
    }

    /**
     * Trigger on volume buttons clicks
     */
    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == ACTION_DOWN && event.repeatCount == 0) {
            val buttonClicked = if (event.keyCode == KEYCODE_VOLUME_UP) {
                ButtonClicked.VOLUME_UP
            } else if (event.keyCode == KEYCODE_VOLUME_DOWN) {
                ButtonClicked.VOLUME_DOWN
            } else {
                return super.onKeyEvent(event)
            }
            coroutineScope.launch(dispatcher) {
                writeLogs(baseContext.getString(R.string.volume_button_clicked))
                if (buttonClicksUseCase(buttonClicked)) {
                    writeLogs(baseContext.getString(R.string.volume_button_reason))
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
        runBlocking {
            setServiceStatusUseCase(false)
        }
        coroutineScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val USB_STATE_ACTION = "android.hardware.usb.action.USB_STATE"
        private const val USB_CONNECTED_EXTRA = "connected"
        private const val AFU_DELAY_AFTER_UNLOCK_MS = 2_000L

        private val PACKAGE_NAMES_INTERCEPTED = setOf("com.android.systemui","com.android.keyguard")
    }
}
