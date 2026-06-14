package ru.sibfu.data.repository.core

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val encryptedPrefs: SharedPreferences
) {
    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
    }

    /**
     * Получаем токен. Используем withContext(Dispatchers.IO),
     * чтобы чтение из файла не блокировало основной поток.
     */
    suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        encryptedPrefs.getString(ACCESS_TOKEN_KEY, null)
    }

    /**
     * Сохраняем токен в зашифрованном виде
     */
    suspend fun saveAccessToken(token: String) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(ACCESS_TOKEN_KEY, token).apply()
    }

    /**
     * Удаляем все данные (например, при Logout)
     */
    suspend fun deleteToken() = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().clear().apply()
    }
}