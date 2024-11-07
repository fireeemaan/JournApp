package com.fireeemaan.journapp.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fireeemaan.journapp.data.datastore.SettingsPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingPref: SettingsPreferences
) : ViewModel() {
    fun getLanguageSetting(): LiveData<String> {
        return settingPref.getLanguageSetting().asLiveData()
    }

    fun saveLanguageSetting(lang: String) {
        viewModelScope.launch {
            settingPref.saveLanguageSetting(lang)
        }
    }
}