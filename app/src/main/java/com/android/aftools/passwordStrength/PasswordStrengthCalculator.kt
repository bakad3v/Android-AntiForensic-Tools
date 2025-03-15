package com.android.aftools.passwordStrength

import com.nulabinc.zxcvbn.Zxcvbn
import org.joda.time.Period
import org.joda.time.format.PeriodFormat
import java.util.Locale
import javax.inject.Inject

/**
 * Class to calculate password strength
 */
class PasswordStrengthCalculator @Inject constructor(private val estimator: Zxcvbn) {

    /**
     * Formatting time to crack password with user's default locale
     */
    private fun passwordCrackTimeToString(crackTime: Double): String {
        val periodFormat = PeriodFormat.wordBased(Locale.getDefault())
        val years = crackTime / SECONDS_IN_YEAR
        if (years.toInt() != 0) {
            val period = Period.years(years.toInt())
            return periodFormat.print(period)
        }
        val days = crackTime / SECONDS_IN_DAY
        if (days.toInt() != 0) {
            val period = Period.days(days.toInt())
            return periodFormat.print(period)
        }
        val hours = crackTime / SECONDS_IN_HOUR
        if (hours.toInt() != 0) {
            val period = Period.hours(hours.toInt())
            return periodFormat.print(period)
        }
        val minutes = crackTime / SECONDS_IN_MINUTE
        if (minutes.toInt() != 0) {
            val period = Period.minutes(minutes.toInt())
            return periodFormat.print(period)
        }
        val period = Period.seconds(crackTime.toInt())
        return periodFormat.print(period)
    }

    /**
     * Calcucate strength of given password
     */
    fun calculatePasswordStrength(password: String): PasswordStrengthData {
        if (password.isBlank()) {
            return PasswordStrengthData()
        }
        val result = estimator.measure(password)
        val strength = when (result.score) {
            0 -> PasswordStrength.WORST
            1 -> PasswordStrength.WEAK
            2 -> PasswordStrength.MEDIUM
            3 -> PasswordStrength.GOOD
            4 -> PasswordStrength.EXCELLENT
            else -> throw RuntimeException("Wrong password score")
        }
        return PasswordStrengthData(
            strength,
            passwordCrackTimeToString(result.crackTimeSeconds.offlineSlowHashing1e4perSecond),
            passwordCrackTimeToString(result.crackTimeSeconds.onlineNoThrottling10perSecond)
        )
    }

    companion object {
        private const val SECONDS_IN_MINUTE = 60
        private const val SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60
        private const val SECONDS_IN_DAY = SECONDS_IN_HOUR * 24
        private const val SECONDS_IN_YEAR = SECONDS_IN_DAY * 365
    }
}