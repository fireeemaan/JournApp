package com.fireeemaan.journapp.ui.login

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.fireeemaan.journapp.R
import com.fireeemaan.journapp.databinding.FragmentLoginBinding
import com.fireeemaan.journapp.ui.edittext.JournEditText

class LoginFragment : Fragment() {

    private lateinit var edEmail: JournEditText
    private lateinit var edPassword: JournEditText
    private lateinit var tvToRegister: TextView

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvToRegister = binding.tvToRegister

        val spannableString = SpannableString(tvToRegister.text)

        val start = tvToRegister.text.indexOf("Register Now!")
        val end = start + "Register Now!".length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

        }
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvToRegister.text = spannableString
        tvToRegister.movementMethod = LinkMovementMethod.getInstance()
    }
}