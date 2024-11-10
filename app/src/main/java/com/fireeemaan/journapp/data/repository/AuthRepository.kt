package com.fireeemaan.journapp.data.repository

import com.fireeemaan.journapp.data.Result
import com.fireeemaan.journapp.data.response.ErrorResponse
import com.fireeemaan.journapp.data.response.LoginResponse
import com.fireeemaan.journapp.data.response.RegisterResponse
import com.fireeemaan.journapp.data.retrofit.auth.AuthApiService
import com.fireeemaan.journapp.utils.EspressoIdlingResource.countingIdlingResource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AuthRepository(private val authApiService: AuthApiService) {
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            countingIdlingResource.increment()
            val response = authApiService.login(email, password)
            if (response.isSuccessful) {
                countingIdlingResource.decrement()
                response.body()?.let { loginResponse ->
                    Result.Success(loginResponse)
                } ?: Result.Error("Empty Response")
            } else {
                countingIdlingResource.decrement()
                val type = object : TypeToken<ErrorResponse>() {}.type
                val errorResponse: ErrorResponse? =
                    Gson().fromJson(response.errorBody()?.charStream(), type)
                Result.Error(errorResponse?.message ?: "Login Failed. Try again later.")
            }
        } catch (e: Exception) {
            Result.Error("Something went wrong.")
        }


    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<RegisterResponse> {
        return try {
            val response = authApiService.register(name, email, password)
            if (response.isSuccessful) {
                response.body()?.let { registerResponse ->
                    Result.Success(registerResponse)
                } ?: Result.Error("Empty Response")
            } else {
                val type = object : TypeToken<ErrorResponse>() {}.type
                val errorResponse: ErrorResponse? =
                    Gson().fromJson(response.errorBody()?.charStream(), type)
                Result.Error(errorResponse?.message ?: "Login Failed. Try again later.")
            }
        } catch (e: Exception) {
            Result.Error("Unexpected Error : ${e.message}")
        }
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(authApiService: AuthApiService): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository(authApiService).also { instance = it }
            }
        }
    }
}