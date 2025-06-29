package com.sonozaki.superuser.mapper

import android.util.Log
import com.sonozaki.superuser.ShellResult
import com.sonozaki.superuser.commandsRunner.CommandResult
import com.topjohnwu.superuser.Shell
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommandResultMapper @Inject constructor() {
    fun mapRootResultToCommandResult(result: Shell.Result): CommandResult {
        return CommandResult(result.out,result.err,result.isSuccess)
    }
    fun mapShizukuResultToCommandResult(result: ShellResult): CommandResult {
        result.output.split("\n").forEach {
            Log.w("adbResult", it)
        }
        return CommandResult(result.output.split("\n"), result.errorOutput.split("\n"),result.isSuccessful)
    }
 }