package com.sonozaki.superuser.commandsRunner

data class CommandResult(val output: List<String>, val error: List<String>, val isSuccess: Boolean)