package com.sonozaki.settings.presentation.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.SettingsDigitItemBinding

class PlainTextSettingView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs) {
        readAttrs(attrs, 0)
    }

    constructor(
        context: Context, attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        readAttrs(attrs,defStyleAttr)
    }

    private var _binding: SettingsDigitItemBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("No settings item binding")

    init {
        val root = inflate(context, R.layout.settings_digit_item,this)
        _binding = SettingsDigitItemBinding.bind(root)
    }

    private fun readAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.settingsText,
            defStyleAttr, 0
        ).apply {
            try {
                binding.mainText.text =
                    this.getText(R.styleable.settingsDigitItem_itemText)?.toString() ?: ""
                val color = this.getColor(R.styleable.settingsText_settingsTextColor,-1)
                if (color!=-1)
                    binding.mainText.setTextColor(color)
            } finally {
                recycle()
            }
        }
    }

    fun setDigit(digit: String) {
        binding.digitField.text = digit
    }

}