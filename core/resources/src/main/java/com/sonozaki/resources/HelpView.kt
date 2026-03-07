package com.sonozaki.resources

import android.content.Context
import android.content.res.TypedArray
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView
import com.sonozaki.resources.databinding.ViewHelpBinding

class HelpView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MaterialCardView(context, attrs, defStyleAttr) {

    private var _binding: ViewHelpBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("No help binding")

    init {
        val root = inflate(context, R.layout.view_help,this)
        _binding = ViewHelpBinding.bind(root)

        val defaultPadding = dp(16f)
        val defaultRadius = dp(16f)
        val defaultStrokeWidth = dp(0f)
        binding.helpBody.movementMethod = LinkMovementMethod.getInstance()

        context.withStyledAttributes(attrs, R.styleable.HelpView) {
            val title = getString(R.styleable.HelpView_helpTitle)
            val text = getString(R.styleable.HelpView_helpText)
            val buttonText = getString(R.styleable.HelpView_buttonText)

            val titleTa = getResourceId(R.styleable.HelpView_helpTitleTextAppearance, 0)
            val bodyTa = getResourceId(R.styleable.HelpView_helpBodyTextAppearance, 0)

            val cornerRadius =
                getDimension(R.styleable.HelpView_helpCornerRadius, defaultRadius)
            val bgColor = getColorOrNull(R.styleable.HelpView_helpBackgroundColor)
            val strokeColor = getColorOrNull(R.styleable.HelpView_helpStrokeColor)
            val strokeWidth =
                getDimension(R.styleable.HelpView_helpStrokeWidth, defaultStrokeWidth)

            val padding =
                getDimension(R.styleable.HelpView_helpContentPadding, defaultPadding)
            val showButton = getBoolean(R.styleable.HelpView_showButton, false)
            if (title != null) setTitle(title)
            if (text != null) setText(text)
            if (buttonText != null) setButtonText(buttonText)

            with(binding) {
                helpButtonView.isVisible = showButton
                if (titleTa != 0) helpTitle.setTextAppearance(titleTa)
                if (bodyTa != 0) helpBody.setTextAppearance(bodyTa)

                setCornerRadiusPx(cornerRadius)

                if (bgColor != null) helpCard.setCardBackgroundColor(bgColor)
                if (strokeColor != null) helpCard.strokeColor = strokeColor
                helpCard.strokeWidth = strokeWidth.toInt()
            }

            setContentPaddingPx(padding.toInt())
        }
    }

    fun setTitle(title: CharSequence) {
        binding.helpTitle.text = title
    }

    fun setText(text: CharSequence) {
        binding.helpBody.text = text
    }

    fun setText(text: Spanned) {
        binding.helpBody.text = text
    }

    fun setButtonOnClickListener(listener: OnClickListener) {
        binding.helpButton.setOnClickListener(listener)
    }

    private fun setButtonText(text: CharSequence) {
        binding.helpButton.text = text
    }

    fun setCornerRadiusPx(px: Float) {
        binding.helpCard.radius = px
    }

    fun setContentPaddingPx(px: Int) {
        binding.helpContent.setPadding(px, px, px, px)
    }

    private fun dp(valueDp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueDp, resources.displayMetrics)

    private fun TypedArray.getColorOrNull(index: Int): Int? =
        if (hasValue(index)) getColor(index, 0) else null
}