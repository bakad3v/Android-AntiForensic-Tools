package com.android.aftools.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.android.aftools.R
import com.android.aftools.databinding.SettingsDigitItemBinding

class SettingsItemNumberView : LinearLayout {
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
            R.styleable.settingsDigitItem,
            defStyleAttr, 0
        ).apply {
            try {
                binding.mainText.text =
                    this.getText(R.styleable.settingsDigitItem_itemText)?.toString() ?: ""
            } finally {
                recycle()
            }
        }
    }

    fun setDigit(digit: String) {
        binding.digitField.text = digit
    }

}