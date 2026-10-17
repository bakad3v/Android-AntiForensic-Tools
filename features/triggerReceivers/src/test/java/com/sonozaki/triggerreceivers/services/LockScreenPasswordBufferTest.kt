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

            assertNull(buffer.insert("p", at = 0))
            assertNull(buffer.insert("${maskingCharacter}a", at = 1))
            assertNull(buffer.insert("$maskingCharacter${maskingCharacter}s", at = 2))
            assertNull(
                buffer.insert(
                    "$maskingCharacter$maskingCharacter${maskingCharacter}s",
                    at = 3
                )
            )

            assertArrayEquals("pass".toCharArray(), buffer.clearField(length = 4))
        }
    }

    @Test
    fun update_clearsBufferBeforeNextPasswordIsEntered() {
        val buffer = LockScreenPasswordBuffer()

        buffer.insert("a", at = 0)
        buffer.insert("•b", at = 1)
        val firstPassword = buffer.clearField(length = 2)

        buffer.insert("c", at = 0)
        buffer.insert("•d", at = 1)
        val secondPassword = buffer.clearField(length = 2)

        assertArrayEquals("ab".toCharArray(), firstPassword)
        assertArrayEquals("cd".toCharArray(), secondPassword)
    }

    @Test
    fun update_ignoresDuplicateEmptyEventsAfterPasswordWasTaken() {
        val buffer = LockScreenPasswordBuffer()

        buffer.insert("a", at = 0)
        val password = buffer.clearField(length = 1)

        assertArrayEquals("a".toCharArray(), password)
        assertNull(buffer.clearField(length = 0))
    }

    @Test
    fun update_handlesInsertionInMiddle() {
        val buffer = bufferContaining("pass")

        assertNull(
            buffer.update(
                text = "•••••",
                fromIndex = 1,
                removedCount = 0,
                addedCount = 1
            )
        )
        assertNull(buffer.updateSnapshot("•x•••"))

        assertArrayEquals("pxass".toCharArray(), buffer.clearField(length = 5))
    }

    @Test
    fun update_handlesDeletionInMiddle() {
        val buffer = bufferContaining("pass")

        assertNull(
            buffer.update(
                text = "•••",
                fromIndex = 1,
                removedCount = 1,
                addedCount = 0
            )
        )

        assertArrayEquals("pss".toCharArray(), buffer.clearField(length = 3))
    }

    @Test
    fun update_handlesReplacementInMiddle() {
        val buffer = bufferContaining("pass")

        assertNull(
            buffer.update(
                text = "•••",
                fromIndex = 1,
                removedCount = 2,
                addedCount = 1
            )
        )
        assertNull(buffer.updateSnapshot("•x•"))

        assertArrayEquals("pxs".toCharArray(), buffer.clearField(length = 3))
    }

    @Test
    fun update_doesNotReturnPasswordWhenInsertedCharactersWereMasked() {
        val buffer = bufferContaining("pass")

        buffer.update(
            text = "••••••",
            fromIndex = 2,
            removedCount = 0,
            addedCount = 2
        )

        assertNull(buffer.clearField(length = 6))
    }

    @Test
    fun updateSnapshot_resolvesMaskedCharactersFromSelectionChangedEvents() {
        val buffer = LockScreenPasswordBuffer()

        assertNull(buffer.insert("•", at = 0))
        assertNull(buffer.updateSnapshot("p"))
        assertNull(buffer.insert("p•", at = 1))
        assertNull(buffer.updateSnapshot("•a"))
        assertNull(buffer.insert("•a•", at = 2))
        assertNull(buffer.updateSnapshot("••s"))
        assertNull(buffer.insert("••s•", at = 3))
        assertNull(buffer.updateSnapshot("•••s"))

        assertArrayEquals("pass".toCharArray(), buffer.updateSnapshot(""))
    }

    private fun bufferContaining(text: String): LockScreenPasswordBuffer {
        return LockScreenPasswordBuffer().also { buffer ->
            text.forEachIndexed { index, character ->
                val snapshot = "•".repeat(index) + character
                buffer.insert(snapshot, at = index)
            }
        }
    }

    private fun LockScreenPasswordBuffer.insert(text: String, at: Int): CharArray? {
        return update(
            text = text,
            fromIndex = at,
            removedCount = 0,
            addedCount = 1
        )
    }

    private fun LockScreenPasswordBuffer.clearField(length: Int): CharArray? {
        return update(
            text = "",
            fromIndex = 0,
            removedCount = length,
            addedCount = 0
        )
    }
}
