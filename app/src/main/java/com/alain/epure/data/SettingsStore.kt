package com.alain.epure.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsStore(private val context: Context) {

    // Falls back to defaults if the stored data is unreadable.
    suspend fun isHomeRolePrompted(): Boolean =
        context.dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { it[HOME_ROLE_PROMPTED] ?: false }
            .first()

    suspend fun markHomeRolePrompted() {
        context.dataStore.edit { it[HOME_ROLE_PROMPTED] = true }
    }

    private companion object {
        val HOME_ROLE_PROMPTED = booleanPreferencesKey("home_role_prompted")
    }
}
