package com.android.aftools

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.aftools.presentation.receivers.DeviceAdminReceiver
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SuperUserTest
{
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun testWorkProfile() {
        val admin = ComponentName(
            context,
            DeviceAdminReceiver::class.java
        )
        val intent = Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE)
        intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_SKIP_ENCRYPTION, true)
        intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME, admin)
        context.startActivity(intent)
    }
}