package com.android.aftools

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private fun Int.toByteArray(): ByteArray = ByteArray(4) { i ->
        (this shr (i * 8) and 0xFF).toByte()
    }

    private fun ByteArray.toInt(): Int =
        this.foldIndexed(0) { i, acc, byte ->
            acc or ((byte.toInt() and 0xFF) shl (i * 8))
        }

    @Test
    fun byteArrayToIntConversion() {
        for (i in 0..50) {
            val int = Random.nextInt()
            val byteRepresentation = int.toByteArray()
            val newInt = byteRepresentation.toInt()
            assertEquals(int, newInt)
        }
    }
}