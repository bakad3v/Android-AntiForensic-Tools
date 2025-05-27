package com.sonozaki.superuser


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class InstallResultReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sessionId = intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1)
        val continuation = completions.remove(sessionId) ?: return

        when (intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1)) {

            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                val confirm = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
                confirm?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(confirm)
                completions[sessionId] = continuation
            }

            PackageInstaller.STATUS_SUCCESS -> continuation.resume(true)

            PackageInstaller.STATUS_FAILURE,
            PackageInstaller.STATUS_FAILURE_ABORTED,
            PackageInstaller.STATUS_FAILURE_INVALID,
            PackageInstaller.STATUS_FAILURE_BLOCKED,
            PackageInstaller.STATUS_FAILURE_CONFLICT,
            PackageInstaller.STATUS_FAILURE_INCOMPATIBLE,
            PackageInstaller.STATUS_FAILURE_STORAGE -> continuation.resume(false)
        }
    }

    companion object {
        internal val completions = mutableMapOf<Int, Continuation<Boolean>>()
    }
}