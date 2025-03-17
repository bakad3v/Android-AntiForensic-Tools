package com.sonozaki.passwordstrength

data class PasswordStrengthData(val strength: PasswordStrength = PasswordStrength.EMPTY, val timeToCrackOffline: String = "", val timeToCrackOnline: String = "")
