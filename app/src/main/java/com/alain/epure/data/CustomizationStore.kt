package com.alain.epure.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
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

// Favourites (in order), custom names and shortcut choices. App keys are "package/class" (see LaunchableApp.key).
class CustomizationStore(private val context: Context) {

    // Unreadable data falls back to defaults (SPEC § 9, robustness).
    val favorites: Flow<List<String>> = read { parseList(it[FAVORITES]) }
    val renames: Flow<Map<String, String>> = read { parseMap(it[RENAMES]) }
    val shortcuts: Flow<Map<String, String>> = read { parseMap(it[SHORTCUTS]) }

    suspend fun saveFavorites(keys: List<String>) {
        context.customizationStore.edit { it[FAVORITES] = JSONArray(keys).toString() }
    }

    suspend fun saveRenames(names: Map<String, String>) {
        context.customizationStore.edit { it[RENAMES] = JSONObject(names).toString() }
    }

    suspend fun saveShortcuts(choices: Map<String, String>) {
        context.customizationStore.edit { it[SHORTCUTS] = JSONObject(choices).toString() }
    }

    private fun <T> read(parse: (Preferences) -> T): Flow<T> =
        context.customizationStore.data
            .catch { emit(emptyPreferences()) }
            .map(parse)

    private fun parseList(json: String?): List<String> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            List(array.length()) { array.getString(it) }
        } catch (e: JSONException) {
            emptyList()
        }
    }

    private fun parseMap(json: String?): Map<String, String> {
        if (json == null) return emptyMap()
        return try {
            val obj = JSONObject(json)
            val map = mutableMapOf<String, String>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = obj.getString(key)
            }
            map
        } catch (e: JSONException) {
            emptyMap()
        }
    }

    private companion object {
        val FAVORITES = stringPreferencesKey("favorites")
        val RENAMES = stringPreferencesKey("renames")
        val SHORTCUTS = stringPreferencesKey("shortcuts")
    }
}
