package com.fireeemaan.journapp.ui.button

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.fireeemaan.journapp.R

class JournButton : AppCompatButton {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var enabledBackground: Drawable =
        ContextCompat.getDrawable(context, R.drawable.button_ripple) as Drawable

    private var disabledBackground: Drawable =
        ContextCompat.getDrawable(context, R.drawable.button_disabled) as Drawable


    init {
        updateButtonState()
        isClickable = true
        isEnabled = true
        textSize = 18f
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        updateButtonState()
    }

    private fun updateButtonState() {
        background = if (isEnabled) enabledBackground else disabledBackground
        val textColor = if (isEnabled) {
            ContextCompat.getColor(context, R.color.md_theme_inversePrimary_highContrast)
        } else {
            ContextCompat.getColor(context, R.color.md_theme_onPrimaryContainer)
        }
        setTextColor(textColor)
    }
}


