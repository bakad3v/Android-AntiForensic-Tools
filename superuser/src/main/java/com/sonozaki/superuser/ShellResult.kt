package com.sonozaki.superuser

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShellResult(
    val exitCode: Int = 0,
    val output: String = "",
    val errorOutput: String = ""
): Parcelable {
    val isSuccessful get() = exitCode == 0
}