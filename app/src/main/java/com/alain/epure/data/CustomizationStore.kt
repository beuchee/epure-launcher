package com.alain.epure.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private val Context.customizationStore by preferencesDataStore(name = "customization")

// Favourites (in order) and custom names. App keys are "package/class" (see LaunchableApp.key).
class CustomizationStore(private val context: Context) {

    // Unreadable data falls back to defaults (SPEC § 9, robustness).
    val favorites: Flow<List<String>> = context.customizationStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> parseFavorites(prefs[FAVORITES]) }

    val renames: Flow<Map<String, String>> = context.customizationStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> parseRenames(prefs[RENAMES]) }

    suspend fun saveFavorites(keys: List<String>) {
        context.customizationStore.edit { it[FAVORITES] = JSONArray(keys).toString() }
    }

    suspend fun saveRenames(names: Map<String, String>) {
        context.customizationStore.edit { it[RENAMES] = JSONObject(names).toString() }
    }

    private fun parseFavorites(json: String?): List<String> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            List(array.length()) { array.getString(it) }
        } catch (e: JSONException) {
            emptyList()
        }
    }

    private fun parseRenames(json: String?): Map<String, String> {
        if (json == null) return emptyMap()
        return try {
            val obj = JSONObject(json)
            val names = mutableMapOf<String, String>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                names[key] = obj.getString(key)
            }
            names
        } catch (e: JSONException) {
            emptyMap()
        }
    }

    private companion object {
        val FAVORITES = stringPreferencesKey("favorites")
        val RENAMES = stringPreferencesKey("renames")
    }
}
