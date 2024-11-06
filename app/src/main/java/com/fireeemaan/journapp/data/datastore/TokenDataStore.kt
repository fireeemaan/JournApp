package com.fireeemaan.journapp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_pref")

class TokenDataStore private constructor(private val dataStore: DataStore<Preferences>) {

    private val AUTH_KEY = stringPreferencesKey(AUTH_TOKEN)

    fun getAuthToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[AUTH_KEY] ?: ""
        }
    }

    suspend fun saveAuthToken(authToken: String) {
        dataStore.edit { preferences -> preferences[AUTH_KEY] = authToken }
    }

    suspend fun clearAuthToken() {
        dataStore.edit { preferences -> preferences.remove(AUTH_KEY) }
    }

    companion object {
        const val AUTH_TOKEN = "auth_token"

        @Volatile
        private var INSTANCE: TokenDataStore? = null
        fun getInstance(dataStore: DataStore<Preferences>): TokenDataStore {
            return INSTANCE ?: synchronized(this) {
                val instance = TokenDataStore(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}