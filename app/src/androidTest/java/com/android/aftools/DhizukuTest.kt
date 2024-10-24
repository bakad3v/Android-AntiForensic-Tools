package com.android.aftools

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.app.admin.IDevicePolicyManager
import android.content.Context
import android.content.IntentSender
import android.content.pm.IPackageInstaller
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Parcel
import android.os.UserHandle
import android.os.UserManager
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.aftools.data.mappers.ProfilesMapper
import com.anggrayudi.storage.extension.toInt
import com.rosan.dhizuku.api.Dhizuku
import com.rosan.dhizuku.api.Dhizuku.binderWrapper
import com.rosan.dhizuku.api.DhizukuBinderWrapper
import com.rosan.dhizuku.api.DhizukuRequestPermissionListener
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.util.concurrent.Executors


@RunWith(AndroidJUnit4::class)
class DhizukuTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val deviceOwner by lazy { Dhizuku.getOwnerComponent() }
    private val profilesMapper = ProfilesMapper()

    @Test
    fun testCheckOwner() {
        val dpm = getDhizukuDPM()
        assert(dpm.isDeviceOwnerApp(context.packageName) && dpm.isAdminActive(deviceOwner))
    }

    @Test
    fun testGetPermission() {
        Dhizuku.init(context)
        Dhizuku.requestPermission(object : DhizukuRequestPermissionListener() {
            override fun onRequestPermission(grantResult: Int) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    assert(true)
                } else {
                    assert(false)
                }
            }
        })
    }

    private fun getUserHandle(userId: Int): UserHandle {
        val parcel = Parcel.obtain()
        parcel.writeInt(userId)
        parcel.setDataPosition(0)
        val user = UserHandle.readFromParcel(parcel)
        parcel.recycle()
        return user
    }

    @SuppressLint("PrivateApi", "SoonBlockedPrivateApi")
    private fun getDhizukuDPM(): DevicePolicyManager {
        if (VERSION.SDK_INT >= 28) HiddenApiBypass.setHiddenApiExemptions("")
        val dhizukuContext = context.createPackageContext(
            Dhizuku.getOwnerComponent().packageName,
            Context.CONTEXT_IGNORE_SECURITY
        )
        val manager =
            dhizukuContext.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val field = manager.javaClass.getDeclaredField("mService")
        field.isAccessible = true
        val oldInterface = field[manager] as IDevicePolicyManager
        if (oldInterface is DhizukuBinderWrapper) return manager
        val oldBinder = oldInterface.asBinder()
        val newBinder = binderWrapper(oldBinder)
        val newInterface = IDevicePolicyManager.Stub.asInterface(newBinder)
        field[manager] = newInterface
        return manager
    }

    private fun getDhizukuPackageInstaller(): PackageInstaller {
        if (VERSION.SDK_INT >= 28) HiddenApiBypass.setHiddenApiExemptions("")
        val context = context.createPackageContext(
            Dhizuku.getOwnerComponent().packageName,
            Context.CONTEXT_IGNORE_SECURITY
        )
        val installer = context.packageManager.packageInstaller
        val field = installer.javaClass.getDeclaredField("mInstaller")
        field.isAccessible = true
        val oldInterface = field[installer] as IPackageInstaller
        if (oldInterface is DhizukuBinderWrapper) return installer
        val oldBinder = oldInterface.asBinder()
        val newBinder = binderWrapper(oldBinder)
        val newInterface = IPackageInstaller.Stub.asInterface(newBinder)
        field[installer] = newInterface
        return installer
    }

    @Test
    fun testUsersCommands() {
        Dhizuku.init(context)
        Dhizuku.requestPermission(object : DhizukuRequestPermissionListener() {
            override fun onRequestPermission(grantResult: Int) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    val dpm = getDhizukuDPM()
                    val idToCompare = 10
                    val userHandles = dpm.getSecondaryUsers(deviceOwner) ?: listOf()
                    val profiles = userHandles.map { profilesMapper.mapUserHandleToProfile(it) }
                    val idToDelete = profiles.find { it.id != 0 }!!.id
                    assertEquals(idToDelete, idToCompare)
                    assert(dpm.removeUser(deviceOwner, getUserHandle(idToDelete)))
                    val newUserHandles = dpm.getSecondaryUsers(deviceOwner) ?: listOf()
                    val newProfiles =
                        newUserHandles.map { profilesMapper.mapUserHandleToProfile(it) }
                    assertEquals(newProfiles.size, 0)
                } else {
                    assert(false)
                }
            }
        })
    }

    @Test
    fun testAppManagement() {
        val packageName = "com.android.aftools"
        assert(
            context.packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
                .find { it.packageName == packageName } != null)
        Log.w(
            "packages",
            context.packageManager.getInstalledPackages(0).map { it.packageName }
                .joinToString { ";" })
        Dhizuku.init(context)
        Dhizuku.requestPermission(object : DhizukuRequestPermissionListener() {
            override fun onRequestPermission(grantResult: Int) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    getDhizukuPackageInstaller().uninstall(
                        packageName,
                        IntentSender.readIntentSenderOrNullFromParcel(Parcel.obtain())
                    )
                    Log.w("deletion", "end")
                } else {
                    assert(false)
                }
            }
        })
    }

    @Test
    fun testideAndClear() {
        Dhizuku.init(context)
        Dhizuku.requestPermission(object : DhizukuRequestPermissionListener() {
            override fun onRequestPermission(grantResult: Int) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    val dpm = getDhizukuDPM()
                    dpm.setApplicationHidden(deviceOwner, "com.android.aftools", true)
                    if (VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        dpm.clearApplicationUserData(
                            deviceOwner,
                            "com.android.aftools",
                            Executors.newSingleThreadExecutor()
                        ) { _, _ -> }
                    }
                } else {
                    assert(false)
                }
            }
        })
    }

    @Test
    fun testDeviceWipe() {
        Dhizuku.init(context)
        Dhizuku.requestPermission(object : DhizukuRequestPermissionListener() {
            override fun onRequestPermission(grantResult: Int) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    var flags = 0
                    val dpm = getDhizukuDPM()
                    val userManager = context.getSystemService(UserManager::class.java)
                    if (VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        flags = flags.or(DevicePolicyManager.WIPE_SILENTLY)
                    if (userManager.isSystemUser && VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        dpm.wipeDevice(flags)
                    } else {
                        dpm.wipeData(flags)
                    }
                } else {
                    assert(false)
                }
            }
        })
    }

    fun DevicePolicyManager.setSafeBootStatus(status: Boolean) {
        addUserRestriction(deviceOwner,UserManager.DISALLOW_SAFE_BOOT)
        setGlobalSetting(deviceOwner, "safe_boot_disallowed",status.toInt().toString())
    }

    fun DevicePolicyManager.getSafeBootStatus(): Boolean {
        return getUserRestrictions(deviceOwner).getBoolean(UserManager.DISALLOW_SAFE_BOOT)
    }

    fun DevicePolicyManager.setSwitchUserRestriction(status: Boolean) {
        addUserRestriction(deviceOwner, UserManager.DISALLOW_USER_SWITCH)
    }

    fun DevicePolicyManager.getSwitchUserRestriction(): Boolean {
        return getUserRestrictions(deviceOwner).getBoolean(UserManager.DISALLOW_USER_SWITCH)
    }

    @Test
    fun switchUserRestrictionTest() {
        Dhizuku.init(context)
        Dhizuku.requestPermission(object : DhizukuRequestPermissionListener() {
            override fun onRequestPermission(grantResult: Int) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    val dpm = getDhizukuDPM()
                    dpm.setSafeBootStatus(true)
                    assert(dpm.getSafeBootStatus())
                    dpm.setSafeBootStatus(false)
                    assert(!dpm.getSafeBootStatus())
                } else {
                    assert(false)
                }
            }
        })
    }

    @Test
    fun safeBootStatusTest() {
        Dhizuku.init(context)
        Dhizuku.requestPermission(object : DhizukuRequestPermissionListener() {
            override fun onRequestPermission(grantResult: Int) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    val dpm = getDhizukuDPM()
                    dpm.setSwitchUserRestriction(true)
                    assert(dpm.getSwitchUserRestriction())
                    dpm.setSwitchUserRestriction(false)
                    assert(!dpm.getSwitchUserRestriction())
                } else {
                    assert(false)
                }
            }
        })
    }

    @Test
    fun clearDhizukuDb() {
        Dhizuku.init(context)
        Dhizuku.requestPermission(object : DhizukuRequestPermissionListener() {
            override fun onRequestPermission(grantResult: Int) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    val dhizukuContext = context.createPackageContext(
                        Dhizuku.getOwnerComponent().packageName,
                        Context.CONTEXT_IGNORE_SECURITY
                    )
                    val dcontext = dhizukuContext.createDeviceProtectedStorageContext()
                    Log.w("deleted",dcontext.getDatabasePath("dhizuku.db").delete().toString())
                    val activityManager = dhizukuContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    activityManager.killBackgroundProcesses(dhizukuContext.packageName)
                } else {
                    assert(false)
                }
            }
        })
    }
}