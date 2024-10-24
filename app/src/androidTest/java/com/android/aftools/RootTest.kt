package com.android.aftools

import android.os.UserManager
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.aftools.data.mappers.ProfilesMapper
import com.android.aftools.presentation.utils.UIText
import com.android.aftools.superuser.superuser.SuperUserException
import com.anggrayudi.storage.extension.toBoolean
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit
import kotlin.time.measureTime

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class RootTest {
    private val profilesMapper = ProfilesMapper()

    private fun executeRootCommand(command: String): Shell.Result {
        val result = Shell.cmd(command).exec()
        if (!result.isSuccess) {
            if (!askSuperUserRights()) {
                throw SuperUserException("", UIText.StringResource(R.string.no_root_rights))
            }
            val resultText = result.out.joinToString(";")
            val errorText = result.err.joinToString(";")
            Log.w("fail", resultText)
            Log.w("fail", errorText)
            throw SuperUserException(
                resultText,
                UIText.StringResource(R.string.unknow_root_error, resultText)
            )
        }
        return result
    }

    private fun askSuperUserRights(): Boolean {
        val result = Shell.cmd("id").exec()
        return result.isSuccess
    }

    @Test
    fun testAskSuperUserRights() {
        assert(askSuperUserRights())
    }

    @Test
    fun testUsersCommands() {
        val oldUsers = executeRootCommand("pm list users").out
        Log.w("time", "start")
        executeRootCommand("pm create-user \"test\"")
        val users = executeRootCommand("pm list users").out
        val id = users.drop(1).map { profilesMapper.mapRootOutputToProfile(it) }
            .find { it.name == "test" }!!.id
        Log.w("ident", id.toString())
        executeRootCommand("pm remove-user $id")
        val usersComparison = executeRootCommand("pm list users").out
        assertEquals(oldUsers, usersComparison)
        Log.w("time", "finish")
    }

    @Test
    fun testAppManagement() {
        val packageName = "com.android.aftools"
        if (executeRootCommand("dpm list-owners").out.any { packageName in it })
            executeRootCommand("dpm remove-active-admin $packageName/.presentation.services.DeviceAdminReceiver")
        executeRootCommand("pm disable $packageName")
        assert(packageName !in executeRootCommand("pm list packages").out)
        executeRootCommand("pm enable $packageName")
        assert(packageName in executeRootCommand("pm list packages").out)
        executeRootCommand("pm uninstall $packageName")
    }

    @Test
    fun testClearAppData() {
        val packageName = "com.android.aftools"
        executeRootCommand("pm clear $packageName")
    }

    /**
     * Conclusion: app uninstallation operations seems to not be parallellised, it's better to execute operations sequentially
     */
    @Test
    fun runAppsDeletionConcurrently() = runTest(timeout = 1.hours) {
        askSuperUserRights()
        val time = measureTime {
            val packages = listOf("me.lucky.silence", "me.lucky.wasted", "me.lucky.duress")
            val coroutines = packages.map {
                launch(Dispatchers.IO) {
                    Log.w("uninstallation","$it uninstalling")
                    executeRootCommand("pm uninstall $it")
                    Log.w("uninstallation","$it uninstalled")
                }
            }
            coroutines.joinAll()
        }
        Log.w("uninstallation_time", time.toString(DurationUnit.MILLISECONDS))
    }

    /**
     * Conclusion: app disabling can't be parallelized too, it's significantly faster than app uninstallation
     */
    @Test
    fun runAppsDisableConcurrently() = runTest(timeout = 1.hours) {
        askSuperUserRights()
        val time = measureTime {
            val packages = listOf("me.lucky.silence", "me.lucky.wasted", "me.lucky.duress")
            val coroutines = packages.map {
                launch(Dispatchers.IO) {
                    Log.w("uninstallation","$it disabling")
                    executeRootCommand("pm disable $it")
                    Log.w("uninstallation","$it disabled")
                }
            }
            coroutines.joinAll()
        }
        Log.w("uninstallation_time", time.toString(DurationUnit.MILLISECONDS))
    }

    /**
     * Conclusion: app's data clearing can't be parallelized too, it's slower than app hiding but faster than uninstallation.
     */
    @Test
    fun runRemoveAppsDataConcurrently() = runTest(timeout = 1.hours) {
        askSuperUserRights()
        val time = measureTime {
            val packages = listOf("me.lucky.silence", "me.lucky.wasted", "me.lucky.duress")
            val coroutines = packages.map {
                launch(Dispatchers.IO) {
                    Log.w("uninstallation","$it clearing")
                    executeRootCommand("pm clear $it")
                    Log.w("uninstallation","$it cleared")
                }
            }
            coroutines.joinAll()
        }
        Log.w("uninstallation_time", time.toString(DurationUnit.MILLISECONDS))
    }


    @Test
    fun runAppsDeletionSequence() {
        askSuperUserRights()
        val time = measureTime {
            val packages = listOf("me.lucky.silence", "me.lucky.wasted", "me.lucky.duress")
            packages.forEach {
                executeRootCommand("pm uninstall $it")
                Log.w("uninstallation","$it uninstalled")
            }
        }
        Log.w("uninstallation_time", time.toString(DurationUnit.MILLISECONDS))
    }

    @Test
    fun runTrim() {
        val result = executeRootCommand("sm fstrim")
        assert(result.isSuccess)
    }

    @Test
    fun testUserRemoveItself() {
        val id = 11
        askSuperUserRights()
        executeRootCommand("pm remove-user $id")
    }

    @Test
    fun userLimitTest() {
        askSuperUserRights()
        executeRootCommand("setprop fw.max_users 5")
        assertEquals(Regex("\\d").find(executeRootCommand("pm get-max-users").out[0])?.value?.toInt(),5)
    }

    @Test
    fun multiuserUIStatusTest() {
        askSuperUserRights()
        executeRootCommand("setprop fw.show_multiuserui 1")
        assert(executeRootCommand("getprop fw.show_multiuserui").out[0].toInt().toBoolean())
        executeRootCommand("setprop fw.show_multiuserui 0")
        assert(!executeRootCommand("getprop fw.show_multiuserui").out[0].toInt().toBoolean())
        executeRootCommand("setprop fw.show_multiuserui 1")
    }

    @Test
    fun safeBootStatusTest() {
        askSuperUserRights()
        executeRootCommand("pm set-user-restriction ${UserManager.DISALLOW_SAFE_BOOT} 1")
        executeRootCommand("settings put global safe_boot_disallowed 1")
        Log.w("device_policy",executeRootCommand("dumpsys device_policy | grep \"${UserManager.DISALLOW_SAFE_BOOT}\"").out[0])
        assert(executeRootCommand("dumpsys device_policy | grep \"no_safe_boot\"").out[0].endsWith("no_safe_boot"))
        executeRootCommand("pm set-user-restriction ${UserManager.DISALLOW_SAFE_BOOT} 0")
        executeRootCommand("settings put global safe_boot_disallowed 0")
        assert(!executeRootCommand("dumpsys device_policy | grep \"${UserManager.DISALLOW_SAFE_BOOT}\"").out[0].endsWith("no_safe_boot"))
    }

    @Test
    fun userSwitcherStatusTest() {
        askSuperUserRights()
        executeRootCommand("settings put global user_switcher_enabled 1")
        assert(executeRootCommand("settings get global user_switcher_enabled").out[0].toInt().toBoolean())
        executeRootCommand("settings put global user_switcher_enabled 0")
        assert(!executeRootCommand("settings get global user_switcher_enabled").out[0].toInt().toBoolean())
        executeRootCommand("settings put global user_switcher_enabled 1")
    }

    private fun getSwitchUserRestriction():Boolean {
        return try {
            executeRootCommand("dumpsys user | grep \"${UserManager.DISALLOW_USER_SWITCH}\"").out[0].endsWith(UserManager.DISALLOW_USER_SWITCH)
        } catch (e: SuperUserException) {
            false
        }
    }

    @Test
    fun switchUserRestrictionTest() {
        askSuperUserRights()
        executeRootCommand("pm set-user-restriction ${UserManager.DISALLOW_USER_SWITCH} 1")
        assert(getSwitchUserRestriction())
        executeRootCommand("pm set-user-restriction ${UserManager.DISALLOW_USER_SWITCH} 0")
        assert(!getSwitchUserRestriction())
    }
}