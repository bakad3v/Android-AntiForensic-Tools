package com.sonozaki.triggerreceivers.services

import javax.inject.Inject

class LockScreenPasswordBuffer @Inject constructor() {
    private val password = StringBuilder()

    /**
     * Updates the reconstructed password and returns it when SystemUI clears the field.
     * The internal buffer is cleared before the returned password can be verified asynchronously.
     */
    fun update(text: String): CharArray? {
        val maskingCharacters = text.count(::isMaskingCharacter)
        if (maskingCharacters == 0 && text.length != 1) {
            return takePassword()
        }

        truncateTo(text.length)
        if (maskingCharacters == text.length) {
            return null
        }

        val visibleCharacterIndex = text.indexOfFirst { !isMaskingCharacter(it) }
        when (visibleCharacterIndex) {
            password.length -> {
                password.append(text[visibleCharacterIndex])
            }
            in password.indices -> {
                password.setCharAt(visibleCharacterIndex, text[visibleCharacterIndex])
            }
        }
        return null
    }

    private fun takePassword(): CharArray? {
        if (password.isEmpty()) {
            return null
        }

        val result = CharArray(password.length) { password[it] }
        clear()
        return result
    }

    private fun truncateTo(length: Int) {
        if (password.length <= length) {
            return
        }

        for (index in length until password.length) {
            password.setCharAt(index, CLEARED_CHARACTER)
        }
        password.delete(length, password.length)
    }

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

        private val MASKING_CHARACTERS = setOf(
            '\u2022', // • Bullet, used by AOSP.
            '\u25CF', // ● Black circle.
            '\u2219', // ∙ Bullet operator.
            '\u002A'  // * Asterisk.
        )
    }
}
