package ru.sibfu.data.repository.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    }

    // Используем runBlocking для интерцептора, так как Interceptor работает в синхронном режиме
    fun getAccessToken(): String? = runBlocking {
        dataStore.data.first()[ACCESS_TOKEN_KEY]
    }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }

    suspend fun deleteToken() {
        dataStore.edit { it.clear() }
    }
}