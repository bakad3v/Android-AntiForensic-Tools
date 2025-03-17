package com.sonozaki.settings.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.widget.CompoundButton
import android.widget.LinearLayout
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.SettingsSwitchItemBinding

class SettingsItemSwitchView : LinearLayout {
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

    private var _binding: SettingsSwitchItemBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("No settings item binding")

    init {
        val root = inflate(context, R.layout.settings_switch_item,this)
        _binding = SettingsSwitchItemBinding.bind(root)
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
                    binding.switchButton.setPadding(0,8,16,8)
                    binding.mainText.setPadding(16,8,8,8)
                } else binding.requirementsText.text = requirementsText
            } finally {
                recycle()
            }
        }
    }

    private fun setCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener?) {
        binding.switchButton.setOnCheckedChangeListener(listener)
    }

    fun setSwitchEnabled(enabled: Boolean) {
        binding.switchButton.isEnabled = enabled
    }

    /**
     * Function for setting switch state without triggering OnCheckedChangeListener
     */
    fun setCheckedProgrammatically(
        value: Boolean,
        listener: CompoundButton.OnCheckedChangeListener
    ) {
        setCheckedChangeListener(null)
        binding.switchButton.isChecked = value
        setCheckedChangeListener(listener)
    }
}