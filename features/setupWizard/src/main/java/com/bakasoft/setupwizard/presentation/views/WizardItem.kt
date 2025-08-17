package com.bakasoft.setupwizard.presentation.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.bakasoft.setupwizard.R
import com.bakasoft.setupwizard.domain.entities.SettingsElementState
import com.google.android.material.card.MaterialCardView

class WizardItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialCardViewStyle
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val startIcon: ImageView
    private val endArrow: ImageView
    private val titleText: TextView

    @ColorInt private val colorOkBase: Int = ContextCompat.getColor(context, R.color.state_ok_base)
    @ColorInt private val colorRequiredBase: Int = ContextCompat.getColor(context, R.color.state_required_base)
    @ColorInt private val colorRecommendedBase: Int = ContextCompat.getColor(context, R.color.state_recommended_base)
    @ColorInt private val colorGrayBase: Int = ContextCompat.getColor(context, R.color.state_gray_base)

    init {
        isClickable = true
        isFocusable = true
        cardElevation = 0f

        LayoutInflater.from(context).inflate(R.layout.wizard_item, this, true)

        startIcon = findViewById(R.id.startIcon)
        endArrow = findViewById(R.id.endArrow)
        titleText = findViewById(R.id.titleText)

        // читаем кастомные атрибуты
        context.theme.obtainStyledAttributes(attrs, R.styleable.SettingsElementView, defStyleAttr, 0).apply {
            try {
                val txt = getText(R.styleable.SettingsElementView_settingsText)
                if (!txt.isNullOrEmpty()) titleText.text = txt
            } finally {
                recycle()
            }
        }

        setState(SettingsElementState.UNKNOW)
    }

    fun setText(text: String) {
        titleText.text = text
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
    }

    fun setState(state: SettingsElementState) {
        val (baseColor, iconRes) = when (state) {
            SettingsElementState.OK -> colorOkBase to R.drawable.baseline_check_24
            SettingsElementState.REQUIRED -> colorRequiredBase to R.drawable.baseline_clear_24
            SettingsElementState.RECOMMENDED -> colorRecommendedBase to R.drawable.baseline_warning_amber_24
            SettingsElementState.NOT_NEEDED -> colorGrayBase to R.drawable.baseline_square_24
            SettingsElementState.UNKNOW -> colorGrayBase to R.drawable.baseline_help_outline_24
        }

        val bgTranslucent = ColorUtils.setAlphaComponent(baseColor, (0.20f * 255).toInt())
        setCardBackgroundColor(bgTranslucent)

        startIcon.setImageResource(iconRes)
        startIcon.imageTintList = ColorStateList.valueOf(baseColor)
    }
}