package com.fireeemaan.journapp.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fireeemaan.journapp.data.datastore.SettingsPreferences

class SettingsViewModelFactory private constructor(
    private val settingPref: SettingsPreferences
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(settingPref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: SettingsViewModelFactory? = null
        fun getInstance(
            settingPref: SettingsPreferences
        ): SettingsViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: SettingsViewModelFactory(settingPref).also { instance = it }
            }
        }
    }

}