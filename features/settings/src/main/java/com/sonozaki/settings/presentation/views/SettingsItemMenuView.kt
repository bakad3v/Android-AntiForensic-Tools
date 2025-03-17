package com.sonozaki.settings.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.SettingsMenuItemBinding

class SettingsItemMenuView : LinearLayout {
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

    private val binding: SettingsMenuItemBinding by lazy { init() }

    val menu get() = binding.showMenu

    fun init(): SettingsMenuItemBinding {
        val root = inflate(context, R.layout.settings_menu_item,this)
        return SettingsMenuItemBinding.bind(root)
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
                val requirementsText = this.getText(R.styleable.settingsSwitchItem_requirementsText)
                if (requirementsText == null) {
                    binding.requirementsText.visibility = GONE
                    binding.mainText.setPadding(8,0,0,8)
                } else binding.requirementsText.text = requirementsText
            } finally {
                recycle()
            }
        }
    }

    fun setText(text: String) {
        binding.showMenu.text = text
    }

}