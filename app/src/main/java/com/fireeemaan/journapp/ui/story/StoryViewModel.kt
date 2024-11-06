package com.fireeemaan.journapp.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fireeemaan.journapp.data.datastore.TokenDataStore
import kotlinx.coroutines.launch

class StoryViewModel(private val authPref: TokenDataStore) : ViewModel() {
    fun clearToken() {
        viewModelScope.launch {
            authPref.clearAuthToken()
        }
    }
}