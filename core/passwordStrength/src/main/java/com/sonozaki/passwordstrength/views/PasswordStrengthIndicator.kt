package com.sonozaki.passwordstrength.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.sonozaki.passwordstrength.R
import com.sonozaki.passwordstrength.databinding.PasswordStrengthIndicatorBinding

/**
 * Progressbars for displaying password strength graphically
 */
class PasswordStrengthIndicator : LinearLayout {
    private var _binding: PasswordStrengthIndicatorBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("No settings item binding")
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs)

    constructor(
        context: Context, attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        val root = inflate(context, R.layout.password_strength_indicator,this)
        _binding = PasswordStrengthIndicatorBinding.bind(root)
    }

    fun setTrackColor(@ColorRes color: Int) {
        binding.progressWorst.setIndicatorColor(ContextCompat.getColor(context, color))
        binding.progressWeak.setIndicatorColor(ContextCompat.getColor(context, color))
        binding.progressMedium.setIndicatorColor(ContextCompat.getColor(context, color))
        binding.progressGood.setIndicatorColor(ContextCompat.getColor(context, color))
        binding.progressExcellent.setIndicatorColor(ContextCompat.getColor(context, color))
    }

    fun setTracksProgress(progress: Int) {
        listOf(
            binding.progressWorst, binding.progressWeak, binding.progressMedium, binding.progressGood, binding.progressExcellent
        ).forEachIndexed { index, linearProgressIndicator ->
            if (index<=progress-1) {
                linearProgressIndicator.progress = 100
            }
            else {
                linearProgressIndicator.progress = 0
            }
        }
    }
}