package com.sonozaki.triggerreceivers.services

import javax.inject.Inject

/**
 * Reconstructs a lock-screen password from a sequence of accessibility text-change events.
 *
 * SystemUI can report one edit as two events: a text-change event with valid edit metadata but a
 * masked inserted character, followed by a selection-change event that exposes the character but
 * has no edit metadata. This buffer applies the structural edit first and then enriches it from the
 * later snapshot.
 */
class LockScreenPasswordBuffer @Inject constructor() {
    // Contains reconstructed characters and UNKNOWN_CHARACTER for values that were never exposed.
    private val password = StringBuilder()

    /**
     * Applies a text edit reported by SystemUI and returns the reconstructed password when the
     * field is cleared.
     *
     * Password accessibility events normally expose unchanged characters as masking characters
     * and reveal only newly entered characters. The edit range is therefore required to preserve
     * characters after insertions and deletions in the middle of the field.
     *
     * If an inserted character is masked or an event cannot be reconciled with the current buffer,
     * unavailable positions are marked as unknown. A password containing an unknown character is
     * never returned for verification.
     * The internal buffer is cleared before the returned password can be verified asynchronously.
     */
    fun update(
        text: String,
        fromIndex: Int,
        removedCount: Int,
        addedCount: Int
    ): CharArray? {
        // SystemUI clears the password field after submitting the entered credential.
        if (text.isEmpty()) {
            return takePassword()
        }

        if (!isValidEdit(text, fromIndex, removedCount, addedCount)) {
            return updateSnapshot(text)
        }

        // Remove the old range first so that characters following it move to their new positions.
        clearRange(fromIndex, removedCount)
        repeat(addedCount) { offset ->
            val index = fromIndex + offset
            // Masked inserted characters cannot be reconstructed safely from this event.
            val character = text[index].takeUnless(::isMaskingCharacter) ?: UNKNOWN_CHARACTER
            password.insert(index, character)
        }

        // Some SystemUI implementations may expose characters outside the reported edit range.
        updateVisibleCharacters(text)
        return null
    }

    /**
     * Applies a password snapshot that has no usable edit metadata.
     *
     * Selection-change events on SystemUI expose the newly entered character only after the
     * corresponding text-change event has inserted a masked placeholder into the buffer.
     */
    fun updateSnapshot(text: String): CharArray? {
        if (text.isEmpty()) {
            return takePassword()
        }

        // A selection event normally has the same length as the preceding text-change event.
        if (isCompatibleSnapshot(text)) {
            updateVisibleCharacters(text)
        } else {
            // Event history was lost. Keep visible characters and mark all masked ones unknown.
            replaceWithSnapshot(text)
        }
        return null
    }

    /** Returns a complete password once and overwrites the retained internal characters. */
    private fun takePassword(): CharArray? {
        if (password.isEmpty() || password.any { it == UNKNOWN_CHARACTER }) {
            clear()
            return null
        }

        val result = CharArray(password.length) { password[it] }
        clear()
        return result
    }

    /** Checks that the reported range transforms the current buffer into the supplied snapshot. */
    private fun isValidEdit(
        text: String,
        fromIndex: Int,
        removedCount: Int,
        addedCount: Int
    ): Boolean {
        return fromIndex >= 0 &&
            removedCount >= 0 &&
            addedCount >= 0 &&
            fromIndex <= password.length &&
            fromIndex + removedCount <= password.length &&
            fromIndex + addedCount <= text.length &&
            password.length - removedCount + addedCount == text.length
    }

    /**
     * Checks whether a snapshot with stale metadata can describe the current buffer. Masking
     * characters match any known value because SystemUI intentionally hides that value.
     */
    private fun isCompatibleSnapshot(text: String): Boolean {
        if (text.length != password.length) {
            return false
        }

        return text.indices.all { index ->
            val character = text[index]
            isMaskingCharacter(character) ||
                password[index] == UNKNOWN_CHARACTER ||
                password[index] == character
        }
    }

    /** Copies every character that SystemUI exposed in the current snapshot. */
    private fun updateVisibleCharacters(text: String) {
        text.forEachIndexed { index, character ->
            if (!isMaskingCharacter(character)) {
                password.setCharAt(index, character)
            }
        }
    }

    /** Rebuilds the buffer from the available snapshot when prior edit history is unusable. */
    private fun replaceWithSnapshot(text: String) {
        clear()
        text.forEach { character ->
            password.append(character.takeUnless(::isMaskingCharacter) ?: UNKNOWN_CHARACTER)
        }
    }

    /** Overwrites removed characters before deleting their range from the mutable buffer. */
    private fun clearRange(fromIndex: Int, count: Int) {
        for (index in fromIndex until fromIndex + count) {
            password.setCharAt(index, CLEARED_CHARACTER)
        }
        password.delete(fromIndex, fromIndex + count)
    }

    /** Overwrites all retained password characters before releasing the backing contents. */
    private fun clear() {
        for (index in password.indices) {
            password.setCharAt(index, CLEARED_CHARACTER)
        }
        password.delete(0, password.length)
    }

    private fun isMaskingCharacter(character: Char): Boolean {
        return character in MASKING_CHARACTERS
    }

    private companion object {
        private const val CLEARED_CHARACTER = '\u0000'
        private const val UNKNOWN_CHARACTER = '\u0000'

        private val MASKING_CHARACTERS = setOf(
            '\u2022', // • Bullet, used by AOSP.
            '\u25CF', // ● Black circle.
            '\u2219', // ∙ Bullet operator.
            '\u002A'  // * Asterisk.
        )
    }
}
