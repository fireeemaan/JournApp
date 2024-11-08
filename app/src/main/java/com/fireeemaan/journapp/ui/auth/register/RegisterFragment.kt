package com.fireeemaan.journapp.ui.auth.register

import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fireeemaan.journapp.R
import com.fireeemaan.journapp.data.Result
import com.fireeemaan.journapp.data.datastore.TokenDataStore
import com.fireeemaan.journapp.data.datastore.dataStore
import com.fireeemaan.journapp.databinding.FragmentRegisterBinding
import com.fireeemaan.journapp.ui.auth.AuthViewModelFactory
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

    private val registerViewModel: RegisterViewModel by viewModels {
        AuthViewModelFactory.getInstance(
            requireContext(),
            TokenDataStore.getInstance(requireContext().applicationContext.dataStore)
        )
    }


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

        setButtonState(isEnabled = false)

        val spannableString = SpannableString(tvToLogin.text)

        val start = tvToLogin.text.indexOf(getString(R.string.login_now))
        val end = start + getString(R.string.login_now).length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvToLogin.text = spannableString
        tvToLogin.movementMethod = LinkMovementMethod.getInstance()

        edName.addTextChangedListener(createTextWatcher())
        edEmail.addTextChangedListener(createTextWatcher())
        edPassword.addTextChangedListener(createTextWatcher())

        registerViewModel.registerResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    setButtonState(isEnabled = false, isLoading = true)
                }

                is Result.Success -> {
                    setButtonState(isEnabled = true)
                    showToast(result.data.message)
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }

                is Result.Error -> {
                    showToast(result.error)
                    setButtonState(isEnabled = true)
                }
            }
        }


        btnRegister.setOnClickListener {
            setButtonState(isEnabled = false, isLoading = true)

            val name = edName.text.toString()
            val email = edEmail.text.toString()
            val password = edPassword.text.toString()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                setButtonState(isEnabled = false)
            } else {
                registerViewModel.register(name, email, password)
            }
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                checkTextError()
            }
        }
    }

    private fun checkTextError() {
        val isNameEmpty = edName.text.isNullOrEmpty()
        val isEmailEmpty = edEmail.text.isNullOrEmpty()
        val isPasswordEmpty = edPassword.text.isNullOrEmpty()

        val isFieldEmpty = isNameEmpty || isEmailEmpty || isPasswordEmpty

        if (edName.error != null || edEmail.error != null || edPassword.error != null || isFieldEmpty) {
            setButtonState(isEnabled = false)
        } else {
            setButtonState(isEnabled = true)
        }
    }

    private fun setButtonState(isEnabled: Boolean, isLoading: Boolean = false) {
        btnRegister.isEnabled = isEnabled
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}