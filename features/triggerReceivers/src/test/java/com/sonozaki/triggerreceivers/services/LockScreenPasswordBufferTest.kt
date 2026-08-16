package com.sonozaki.triggerreceivers.services

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LockScreenPasswordBufferTest {

    @Test
    fun update_reconstructsPasswordWithSupportedMaskingCharacters() {
        val maskingCharacters = listOf('\u2022', '\u25CF', '\u2219', '\u002A')

        maskingCharacters.forEach { maskingCharacter ->
            val buffer = LockScreenPasswordBuffer()

            assertNull(buffer.update("p"))
            assertNull(buffer.update("${maskingCharacter}a"))
            assertNull(buffer.update("$maskingCharacter${maskingCharacter}s"))
            assertNull(
                buffer.update(
                    "$maskingCharacter$maskingCharacter${maskingCharacter}s"
                )
            )

            assertArrayEquals("pass".toCharArray(), buffer.update(""))
        }
    }

    @Test
    fun update_clearsBufferBeforeNextPasswordIsEntered() {
        val buffer = LockScreenPasswordBuffer()

        buffer.update("a")
        buffer.update("•b")
        val firstPassword = buffer.update("")

        buffer.update("c")
        buffer.update("•d")
        val secondPassword = buffer.update("")

        assertArrayEquals("ab".toCharArray(), firstPassword)
        assertArrayEquals("cd".toCharArray(), secondPassword)
    }

    @Test
    fun update_ignoresDuplicateEmptyEventsAfterPasswordWasTaken() {
        val buffer = LockScreenPasswordBuffer()

        buffer.update("a")
        val password = buffer.update("")

        assertArrayEquals("a".toCharArray(), password)
        assertNull(buffer.update(""))
    }
}
