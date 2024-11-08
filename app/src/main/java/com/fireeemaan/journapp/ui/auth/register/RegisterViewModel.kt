package com.fireeemaan.journapp.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fireeemaan.journapp.data.Result
import com.fireeemaan.journapp.data.repository.AuthRepository
import com.fireeemaan.journapp.data.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerResponse = MutableLiveData<Result<RegisterResponse>>()
    val registerResponse: LiveData<Result<RegisterResponse>> get() = _registerResponse

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerResponse.postValue(Result.Loading)
            val result = authRepository.register(name, email, password)
            _registerResponse.postValue(result)
        }
    }
}