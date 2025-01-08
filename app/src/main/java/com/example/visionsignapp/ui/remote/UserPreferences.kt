package com.example.visionsignapp.ui.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("auth_token")
        val EMAIL_KEY = stringPreferencesKey("user_email")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val AVATAR_URL_KEY = stringPreferencesKey("avatar_url")
    }

    val authToken: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[TOKEN_KEY] }

    val userEmail: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[EMAIL_KEY] }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[IS_LOGGED_IN] ?: false }

    val avatarUrl: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[AVATAR_URL_KEY] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[IS_LOGGED_IN] = true
        }
    }

    suspend fun clearToken() {
            context.dataStore.edit { preferences ->
                preferences.remove(TOKEN_KEY)
                preferences[IS_LOGGED_IN] = false
            }
    }

    suspend fun getToken(): String? {
        return authToken.first()
    }

    suspend fun getAvatarUrl(): String? {
        return avatarUrl.first()
    }

    suspend fun saveAvatarUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[AVATAR_URL_KEY] = url
        }
    }
}
