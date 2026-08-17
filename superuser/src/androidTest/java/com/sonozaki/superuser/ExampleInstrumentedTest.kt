package com.sonozaki.superuser

import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sonozaki.superuser.shizuku.ShizukuShell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.sonozaki.superuser.test", appContext.packageName)
    }

    @Test
    fun streamsProcessInputAndWaitsForResult() = runBlocking {
        val payload = "test-only-apk-stream"
        val shell = ShizukuShell()
        val processId = shell.execute("cat")
        val processInput = requireNotNull(shell.processInput(processId))
        val result = async(Dispatchers.IO) { shell.waitForProcess(processId) }

        ParcelFileDescriptor.AutoCloseOutputStream(processInput).use { output ->
            output.write(payload.toByteArray())
        }

        val shellResult = result.await()
        assertTrue(shellResult.errorOutput, shellResult.isSuccessful)
        assertEquals(payload, shellResult.output)
    }
}
