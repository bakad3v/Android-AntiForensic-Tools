package com.sonozaki.superuser.root

import android.content.ComponentName
import android.content.Context
import android.os.UserManager
import com.sonozaki.resources.IO_DISPATCHER
import com.sonozaki.superuser.R
import com.sonozaki.superuser.commandsRunner.CommandResult
import com.sonozaki.superuser.commandsRunner.CommandsRunner
import com.sonozaki.superuser.domain.usecases.SetRootInactiveUseCase
import com.sonozaki.superuser.mapper.CommandResultMapper
import com.sonozaki.superuser.mapper.ProfilesMapper
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.utils.UIText
import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class Root @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profilesMapper: ProfilesMapper,
    private val setRootInactiveUseCase: SetRootInactiveUseCase,
    private val userManager: UserManager,
    private val deviceAdmin: ComponentName,
    @Named(IO_DISPATCHER) private val coroutineDispatcher: CoroutineDispatcher,
    private val commandResultMapper: CommandResultMapper
) : CommandsRunner(context, coroutineDispatcher, profilesMapper, userManager, deviceAdmin) {

    private var isShellChecked = false

    private fun checkShell() {
        if (isShellChecked) {
            return
        }
        //su may become inaccessible if persist.sys.safemode == 1 even when booting to safe mode is prohibited
        try {
            Runtime.getRuntime().exec("su")
        } catch (e: IOException) {
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setCommands(MAGISK_DEFAULT)
            )
        } finally {
            isShellChecked = true
        }
    }

    override suspend fun runCommand(command: String): CommandResult {
        return commandResultMapper.mapRootResultToCommandResult(executeRootCommand(command))
    }

    override suspend fun executeRootCommand(command: String): Shell.Result = withContext(coroutineDispatcher) {
        checkShell()
        val result = Shell.cmd(command).exec()
        if (!result.isSuccess) {
            if (!askSuperUserRights()) {
                setRootInactiveUseCase()
                throw SuperUserException(
                    NO_ROOT_RIGHTS,
                    UIText.StringResource(com.sonozaki.resources.R.string.no_root_rights)
                )
            }
            val resultText = result.out.joinToString(";")
            throw SuperUserException(
                resultText,
                UIText.StringResource(R.string.unknow_root_error, resultText)
            )
        }
        return@withContext result
    }

    private fun executeRootCommandParallelly(command: String, callback: (String) -> Unit): () -> Unit {
        val shellBuilder = Shell.Builder.create()
        val shell =  try {
            Runtime.getRuntime().exec("su")
            shellBuilder.build()
        } catch (e: IOException) {
            shellBuilder
                .setCommands(MAGISK_DEFAULT).build()
        }
        val callbackList: List<String?> = object : CallbackList<String?>() {
            override fun onAddElement(s: String?) {
                callback(s?:"")
            }
        }
        val job = shell.newJob()
        job.add(command).to(callbackList).submit()
        return { shell.close() }
    }

    override fun getPowerButtonClicks(callback: (Boolean) -> Unit): () -> Unit {
        return executeRootCommandParallelly("getevent -lq") {
            callback(it.contains("KEY_POWER") && it.trimEnd().endsWith("DOWN"))
        }
    }

    fun askSuperUserRights(): Boolean {
        checkShell()
        val result = Shell.cmd("id").exec()
        return result.out[0].startsWith("uid=0(root)")
    }

    companion object {
        private const val NO_ROOT_RIGHTS = "App doesn't have root rights"
        private const val MAGISK_DEFAULT = "/debug_ramdisk/su"
    }
}