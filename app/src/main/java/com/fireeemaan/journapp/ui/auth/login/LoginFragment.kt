package com.fireeemaan.journapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
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
import com.fireeemaan.journapp.databinding.FragmentLoginBinding
import com.fireeemaan.journapp.ui.auth.AuthViewModelFactory
import com.fireeemaan.journapp.ui.button.JournButton
import com.fireeemaan.journapp.ui.edittext.JournEditText
import com.fireeemaan.journapp.ui.story.StoryActivity

class LoginFragment : Fragment() {

    private lateinit var edEmail: JournEditText
    private lateinit var edPassword: JournEditText
    private lateinit var tvToRegister: TextView
    private lateinit var btnLogin: JournButton

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels {
        AuthViewModelFactory.getInstance(
            requireContext(),
            TokenDataStore.getInstance(requireContext().applicationContext.dataStore)
        )
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

        checkLoginStatus()

        tvToRegister = binding.tvToRegister
        edPassword = binding.edLoginPassword
        edEmail = binding.edLoginEmail
        btnLogin = binding.btnLogin

        edPassword.inputMode = JournEditText.InputMode.PASSWORD
        edEmail.inputMode = JournEditText.InputMode.EMAIL

        setButtonState(isEnabled = false)

        val spannableString = SpannableString(tvToRegister.text)

        val start = tvToRegister.text.indexOf(getString(R.string.register_now))
        val end = start + getString(R.string.register_now).length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvToRegister.text = spannableString
        tvToRegister.movementMethod = LinkMovementMethod.getInstance()

        edEmail.addTextChangedListener(createTextWatcher())
        edPassword.addTextChangedListener(createTextWatcher())

        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    setButtonState(isEnabled = false, isLoading = true)
                }

                is Result.Success -> {
                    setButtonState(isEnabled = true)
                    val token = result.data.loginResult.token
                    loginViewModel.saveAuthToken(token)
                    val intent = Intent(requireContext(), StoryActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }

                is Result.Error -> {
                    showToast(result.error)
                    setButtonState(isEnabled = true)
                }
            }
        }

        btnLogin.setOnClickListener {
            setButtonState(isEnabled = false, isLoading = true)

            val email = edEmail.text.toString()
            val password = edPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                setButtonState(isEnabled = false)
            } else {
                loginViewModel.login(email, password)
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

    private fun checkLoginStatus() {
        loginViewModel.getAuthToken().observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                val intent = Intent(requireContext(), StoryActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun checkTextError() {
        val isEmailEmpty = edEmail.text.isNullOrEmpty()
        val isPasswordEmpty = edPassword.text.isNullOrEmpty()

        if (edEmail.error != null || edPassword.error != null || isEmailEmpty || isPasswordEmpty) {
            setButtonState(isEnabled = false)
        } else {
            setButtonState(isEnabled = true)
        }
    }

    private fun setButtonState(isEnabled: Boolean, isLoading: Boolean = false) {
        btnLogin.isEnabled = isEnabled
        btnLogin.isClickable = isEnabled
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