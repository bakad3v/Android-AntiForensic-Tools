package com.sonozaki.settings.presentation.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.SettingsItemBinding

class SettingsItemView: ConstraintLayout {
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

    private var _binding: SettingsItemBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("No settings item binding")

    init {
        val root = inflate(context, R.layout.settings_item,this)
        _binding = SettingsItemBinding.bind(root)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        binding.icon.setOnClickListener(l)
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

}