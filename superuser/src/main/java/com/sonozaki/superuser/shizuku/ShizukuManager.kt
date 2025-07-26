package com.sonozaki.superuser.shizuku

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.UserManager
import android.util.Log
import com.sonozaki.entities.ShizukuState
import com.sonozaki.resources.IO_DISPATCHER
import com.sonozaki.superuser.IRemoteShell
import com.sonozaki.superuser.R
import com.sonozaki.superuser.ShellResult
import com.sonozaki.superuser.commandsRunner.CommandResult
import com.sonozaki.superuser.commandsRunner.CommandsRunner
import com.sonozaki.superuser.domain.usecases.GetPermissionsFlowUseCase
import com.sonozaki.superuser.domain.usecases.SetShizukuPermissionUseCase
import com.sonozaki.superuser.mapper.CommandResultMapper
import com.sonozaki.superuser.mapper.ProfilesMapper
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.utils.UIText
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.BufferedSource
import rikka.shizuku.Shizuku
import rikka.shizuku.shared.BuildConfig
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ShizukuManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named(IO_DISPATCHER) private val coroutineDispatcher: CoroutineDispatcher,
    private val coroutineScope: CoroutineScope,
    private val _shizukuStateFlow: MutableStateFlow<ShizukuState>,
    private val getPermissionsFlowUseCase: GetPermissionsFlowUseCase,
    private val setShizukuPermissionUseCase: SetShizukuPermissionUseCase,
    private val deviceAdmin: ComponentName,
    private val userManager: UserManager,
    private val profilesMapper: ProfilesMapper,
    private val commandResultMapper: CommandResultMapper
): CommandsRunner(context, coroutineDispatcher, profilesMapper, userManager, deviceAdmin) {

    val shizukuStateFlow = _shizukuStateFlow.asStateFlow()

    init {
        //listen for shizuku permission infinitely
        coroutineScope.launch {
            getPermissionsFlowUseCase().collect {
                if (it.isShizuku) {
                    start()
                } else {
                    stop()
                }
            }
        }
    }

    private var userService: IRemoteShell? = null

    private val userServiceArgs by lazy {
        Shizuku.UserServiceArgs(ComponentName(context, ShizukuShell::class.java))
            .daemon(false)
            .processNameSuffix("my_service")
            .debuggable(BuildConfig.DEBUG)
            .version(
                context.run {
                    packageManager.getPackageInfo(packageName, 0).versionCode
                }
            )
            .tag(context.packageName)
    }


    override suspend fun runCommand(command: String): CommandResult {
        return commandResultMapper.mapShizukuResultToCommandResult(runAdbCommand(command))
    }

    /**
     * Wait until shizuku service stops loading, run command if possible
     */
    private suspend fun runAdbCommand(command: String) = withContext(coroutineDispatcher) {
        val nextState = shizukuStateFlow.first {
            it != ShizukuState.LOADING
        }
        if (nextState == ShizukuState.INITIALIZED) {
            val result = userService?.executeNow(command)
            result ?: ShellResult(3)
        } else { //if service is dead return error
            ShellResult(3)
        }
    }

    /**
     * Run adb command if possible without waiting
     */
    private suspend fun runAdbCommandImmediately(command: String) = withContext(coroutineDispatcher) {
        if (shizukuStateFlow.value == ShizukuState.INITIALIZED) {
            val result = userService?.executeNow(command)
            result ?: ShellResult(3)
        } else { //if service is dead or shizuku inactive return error
            ShellResult(3)
        }
    }

    fun checkShizukuInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo("moe.shizuku.privileged.api", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun requestShizukuPermission() {
        val listener = object : Shizuku.OnRequestPermissionResultListener {
            override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                if (requestCode != SHIZUKU_PERMISSION_REQUEST_ID) {
                    coroutineScope.launch {
                        setShizukuPermissionUseCase(false)
                    }
                }
                when (grantResult == PackageManager.PERMISSION_GRANTED) {
                    true -> {
                        coroutineScope.launch {
                            setShizukuPermissionUseCase(true)
                        }
                    }
                    else -> {
                        coroutineScope.launch {
                            setShizukuPermissionUseCase(false)
                        }
                    }
                }
                Shizuku.removeRequestPermissionResultListener(this)
            }
        }
        Shizuku.addRequestPermissionResultListener(listener)
        Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_ID)
    }

    /**
     * Bind shizuku user service, listen for shizuku connection state and change shizuku state accordingly
     */
    private fun start() {
        object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
                Log.w("binderReceivedListener","connected")
                if (binder == null || !binder.pingBinder()) {
                    _shizukuStateFlow.value = ShizukuState.DEAD
                    return
                }
                Log.w("binderReceivedListener","connectedSuccess")
                userService = IRemoteShell.Stub.asInterface(binder)
                _shizukuStateFlow.value = ShizukuState.INITIALIZED
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {
                userService = null
                _shizukuStateFlow.value = ShizukuState.DEAD
            }
        }.also {
            _shizukuStateFlow.value = ShizukuState.LOADING
            try {
                Shizuku.bindUserService(userServiceArgs, it)
            } catch (e: IllegalStateException) {
                _shizukuStateFlow.value = ShizukuState.DEAD
            } catch (e: SecurityException) {
                _shizukuStateFlow.value = ShizukuState.DEAD
                coroutineScope.launch {
                    setShizukuPermissionUseCase(false)
                }
            }
        }
    }

    /**
     * Unbind shizuku user service
     */
    fun stop() {
        runCatching {
            // With true flag it does not remove connection from cache
            Shizuku.unbindUserService(userServiceArgs, null, false)
            Shizuku.unbindUserService(userServiceArgs, null, true)
            userService = null
            _shizukuStateFlow.value = ShizukuState.DEAD
        }
    }

    override suspend fun executeRootCommand(command: String): Shell.Result {
        throw SuperUserException(NOT_ENOUGH_RIGHTS, UIText.StringResource(R.string.not_enough_rights))
    }

    override fun getPowerButtonClicks(callback: (Boolean) -> Unit): () -> Unit {
        throw SuperUserException(NOT_ENOUGH_RIGHTS, UIText.StringResource(R.string.not_enough_rights))
    }

    override suspend fun installTestOnlyApp(
        length: Long,
        data: BufferedSource
    ): Boolean {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights))
    }

    override suspend fun stopLogd() {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights))
    }

    override suspend fun setMultiuserUI(status: Boolean) {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights))
    }

    override suspend fun getMultiuserUIStatus(): Boolean {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights))
    }

    override suspend fun setUsersLimit(limit: Int) {
        throw SuperUserException(NO_ROOT_RIGHTS, UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights))
    }

    override suspend fun hideApp(packageName: String) {
        throw SuperUserException(NOT_ENOUGH_RIGHTS, UIText.StringResource(R.string.not_enough_rights))
    }

    override suspend fun clearAppData(packageName: String) {
        throw SuperUserException(NOT_ENOUGH_RIGHTS, UIText.StringResource(R.string.not_enough_rights))
    }

    override suspend fun runTrim() {
        runAdbCommandImmediately("sm fstrim &")
    }

    override suspend fun wipe() {
        throw SuperUserException(NOT_ENOUGH_RIGHTS, UIText.StringResource(R.string.not_enough_rights))
    }

    override suspend fun removeProfile(id: Int) {
        throw SuperUserException(NOT_ENOUGH_RIGHTS, UIText.StringResource(R.string.not_enough_rights))
    }

    override suspend fun getSwitchUserRestriction(): Boolean {
        throw SuperUserException(NOT_ENOUGH_RIGHTS, UIText.StringResource(R.string.not_enough_rights))
    }

    override suspend fun setSwitchUserRestriction(status: Boolean) {
        throw SuperUserException(NOT_ENOUGH_RIGHTS, UIText.StringResource(R.string.not_enough_rights))
    }

    companion object {
        private const val SHIZUKU_PERMISSION_REQUEST_ID = 18
        private const val NOT_ENOUGH_RIGHTS = "App doesn't have necessary rights"
        private const val NO_ROOT_RIGHTS = "App doesn't have root rights"
    }
}