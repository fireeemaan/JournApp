package com.fireeemaan.journapp.ui.edittext

import android.content.Context
import android.graphics.Canvas
import android.text.InputType
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.fireeemaan.journapp.R

class JournEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {


    var inputMode: InputMode = InputMode.DEFAULT
        set(value) {
            field = value
            updateInputType()
        }

    enum class InputMode {
        EMAIL, PASSWORD, DEFAULT
    }

    init {
        addTextChangedListener { text ->
            if (text.toString().isNotEmpty()) {
                when (inputMode) {
                    InputMode.EMAIL -> checkEmail(text.toString())
                    InputMode.PASSWORD -> checkPassword(text.toString())
                    InputMode.DEFAULT -> error = null
                }
            }
        }

        updateInputType()
        setupEditTextStyle()
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }


    private fun updateInputType() {
        when (inputMode) {
            InputMode.DEFAULT -> {
                inputType = InputType.TYPE_CLASS_TEXT
                hint = "Enter Text"
            }

            InputMode.EMAIL -> {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                hint = "Enter Email"
            }

            InputMode.PASSWORD -> {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                hint = "Enter Password"
            }
        }
    }

    private fun setupEditTextStyle() {
        setPaddingRelative(40, 30, 40, 30)
        textSize = 18f
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        background = ContextCompat.getDrawable(context, R.drawable.edit_text_bg)
    }

    private fun checkEmail(email: String) {
        error = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            context.getString(R.string.error_email)
        } else {
            null
        }
    }

    private fun checkPassword(password: String) {
        error = if (password.length <= 8) {
            context.getString(R.string.error_password)
        } else {
            null
        }
    }
}