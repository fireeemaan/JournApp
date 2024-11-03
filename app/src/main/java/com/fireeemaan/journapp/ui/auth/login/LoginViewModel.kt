package com.fireeemaan.journapp.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fireeemaan.journapp.data.repository.AuthRepository
import com.fireeemaan.journapp.data.response.LoginResponse
import kotlinx.coroutines.launch
import com.fireeemaan.journapp.data.Result
import com.fireeemaan.journapp.data.datastore.TokenDataStore
import com.fireeemaan.journapp.ui.auth.AuthViewModelFactory

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val authPref: TokenDataStore
) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.postValue(Result.Loading)

            val result = authRepository.login(email, password)
            if (result is Result.Success) {
                authPref.saveAuthToken(result.data.loginResult.token)
            }
            _loginResult.postValue(result)
        }
    }
}