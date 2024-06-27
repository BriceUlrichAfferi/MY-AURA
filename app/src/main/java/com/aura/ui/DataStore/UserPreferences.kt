package com.aura.ui.DataStore

import androidx.datastore.core.DataStore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserPreferences {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

    private val USER_IDENTIFIER_KEY = stringPreferencesKey("USER_IDENTIFIER")

    suspend fun saveUserIdentifier(context: Context, identifier: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_IDENTIFIER_KEY] = identifier
        }
    }

    fun getUserIdentifier(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_IDENTIFIER_KEY]
        }
    }
}
