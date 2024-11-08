package com.fireeemaan.journapp.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fireeemaan.journapp.data.datastore.TokenDataStore
import com.fireeemaan.journapp.data.repository.AuthRepository
import com.fireeemaan.journapp.di.Injection
import com.fireeemaan.journapp.ui.auth.login.LoginViewModel
import com.fireeemaan.journapp.ui.auth.register.RegisterViewModel

class AuthViewModelFactory private constructor(
    private val authRepository: AuthRepository,
    private val authPref: TokenDataStore
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository, authPref) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(authRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: AuthViewModelFactory? = null
        fun getInstance(context: Context, authPref: TokenDataStore): AuthViewModelFactory =
            instance ?: synchronized(this) {
                val authRepository = Injection.provideAuthRepository(context)
                instance ?: AuthViewModelFactory(authRepository, authPref)
            }.also { instance = it }
    }
}