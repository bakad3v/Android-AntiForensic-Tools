package com.sonozaki.settings.presentation.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import com.google.android.material.color.MaterialColors
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.SettingsImageItemBinding

class SettingsItemImageView : LinearLayout {
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

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        binding.icon.setOnClickListener(l)
    }

    private var _binding: SettingsImageItemBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("No settings item binding")

    init {
        val root = inflate(context, R.layout.settings_image_item,this)
        _binding = SettingsImageItemBinding.bind(root)
    }

    private fun readAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.settingsSwitchItem,
            defStyleAttr, 0
        ).apply {
            try {
                binding.mainText.text =
                    this.getText(R.styleable.settingsSwitchItem_mainText)?.toString() ?: ""
                binding.requirementsText.text =
                    this.getText(R.styleable.settingsSwitchItem_requirementsText) ?: ""
            } finally {
                recycle()
            }
        }
    }

    fun setActive(active: Boolean) {
        val color = if (active) {
            val color = MaterialColors.getColor(
                binding.icon,
                com.google.android.material.R.attr.colorPrimary
            )
            ColorStateList.valueOf(color)
        } else ColorStateList.valueOf(Color.GRAY)
        binding.icon.iconTint = color
        isClickable = active
    }
}