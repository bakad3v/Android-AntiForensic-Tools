package com.sonozaki.passwordstrength

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

enum class PasswordStrength(@StringRes val commentary: Int, @ColorRes val color: Int) {
    EMPTY(R.string.empty, R.color.defaultColor), WORST(R.string.worst, R.color.worstMeterColor), WEAK(R.string.weak, R.color.weakMeterColor), MEDIUM(R.string.medium, R.color.mediumMeterColor), GOOD(R.string.good, R.color.strongMeterColor), EXCELLENT(R.string.excellent, R.color.excellentMeterColor)
}