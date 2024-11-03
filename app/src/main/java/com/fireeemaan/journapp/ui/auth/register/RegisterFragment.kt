package com.fireeemaan.journapp.ui.auth.register

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.set
import androidx.navigation.fragment.findNavController
import com.fireeemaan.journapp.R
import com.fireeemaan.journapp.databinding.FragmentRegisterBinding
import com.fireeemaan.journapp.ui.button.JournButton
import com.fireeemaan.journapp.ui.edittext.JournEditText


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var tvToLogin: TextView
    private lateinit var edName: JournEditText
    private lateinit var edEmail: JournEditText
    private lateinit var edPassword: JournEditText
    private lateinit var btnRegister: JournButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvToLogin = binding.tvToLogin
        edName = binding.edRegisterName
        edEmail = binding.edRegisterEmail
        edPassword = binding.edRegisterPassword
        btnRegister = binding.btnRegister

        edName.hint = "Enter Your Name"
        edEmail.inputMode = JournEditText.InputMode.EMAIL
        edPassword.inputMode = JournEditText.InputMode.PASSWORD

        val spannableString = SpannableString(tvToLogin.text)

        val start = tvToLogin.text.indexOf("Login Now!")
        val end = start + "Login Now!".length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvToLogin.text = spannableString
        tvToLogin.movementMethod = LinkMovementMethod.getInstance()

        btnRegister.setOnClickListener {
            
        }
    }
}