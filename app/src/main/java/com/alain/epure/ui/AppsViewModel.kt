package com.alain.epure.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alain.epure.data.AppRepository
import com.alain.epure.data.CustomizationStore
import com.alain.epure.domain.AppSearch
import com.alain.epure.domain.Customization
import com.alain.epure.domain.FavoriteSlot
import com.alain.epure.domain.LaunchableApp
import com.alain.epure.domain.Shortcut
import com.alain.epure.domain.ShortcutChoice
import com.alain.epure.domain.shortcutChoiceFrom
import com.alain.epure.system.Shortcuts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// An app with a custom name: the original app and the name shown instead (F-2.4).
data class RenamedEntry(val app: LaunchableApp, val customName: String)

class AppsViewModel(
    private val repository: AppRepository,
    private val customization: CustomizationStore
) : ViewModel() {

    private val apps = MutableStateFlow<List<LaunchableApp>>(emptyList())
    private val renames = customization.renames
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())
    private val favoriteKeys = customization.favorites
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    private val shortcutValues = customization.shortcuts
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // Apps as shown everywhere: the custom name replaces the system name (DEC-0010).
    val results: StateFlow<List<LaunchableApp>> =
        combine(apps, renames, _query) { list, names, text ->
            AppSearch.filter(list.map { it.withName(names) }, text)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Favourites in order, with spaces. An uninstalled app simply disappears.
    val favorites: StateFlow<List<FavoriteSlot>> =
        combine(apps, favoriteKeys, renames) { list, keys, names ->
            keys.mapNotNull { key ->
                if (key == Customization.SPACE_KEY) {
                    FavoriteSlot(key, null)
                } else {
                    list.find { it.key == key }?.let { FavoriteSlot(key, it.withName(names)) }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val renamedApps: StateFlow<List<RenamedEntry>> =
        combine(apps, renames) { list, names ->
            list.filter { it.key in names }.map { RenamedEntry(it, names.getValue(it.key)) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // What each shortcut opens right now (DEC-0013).
    val shortcutChoices: StateFlow<Map<Shortcut, ShortcutChoice>> =
        combine(apps, shortcutValues) { list, values ->
            Shortcut.entries.associateWith { shortcutChoiceFrom(values[it.name], list) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, Shortcut.entries.associateWith { ShortcutChoice.Default })

    // Value is "default", "off", or an app key.
    fun setShortcut(shortcut: Shortcut, value: String) {
        val updated = shortcutValues.value + (shortcut.name to value)
        viewModelScope.launch { customization.saveShortcuts(updated) }
    }

    fun launchShortcut(shortcut: Shortcut) {
        when (val choice = shortcutChoices.value[shortcut]) {
            is ShortcutChoice.App -> repository.launch(choice.app)
            ShortcutChoice.Default -> repository.launchSystem(Shortcuts.defaultIntent(shortcut))
            ShortcutChoice.Off, null -> Unit
        }
    }

    fun refresh() {
        val loaded = repository.loadApps()
        apps.value = loaded
        // An empty list may mean the system did not answer: never clean up data in that case.
        if (loaded.isNotEmpty()) {
            viewModelScope.launch { cleanUp(loaded) }
        }
    }

    fun onQueryChange(text: String) {
        _query.value = text
    }

    fun clearQuery() {
        _query.value = ""
    }

    fun launch(app: LaunchableApp) {
        repository.launch(app)
    }

    fun openAppDetails(app: LaunchableApp) {
        repository.openAppDetails(app)
    }

    fun isFavorite(app: LaunchableApp): Boolean = app.key in favoriteKeys.value

    // Returns false when the favourites are full and the app cannot be added.
    fun toggleFavorite(app: LaunchableApp): Boolean {
        val current = favoriteKeys.value
        val updated = when {
            app.key in current -> current - app.key
            Customization.canAddFavorite(current.size) -> current + app.key
            else -> return false
        }
        viewModelScope.launch { customization.saveFavorites(updated) }
        return true
    }

    // Returns false when the favourites are full.
    fun addSpace(): Boolean {
        val current = favoriteKeys.value
        if (!Customization.canAddFavorite(current.size)) return false
        viewModelScope.launch { customization.saveFavorites(current + Customization.SPACE_KEY) }
        return true
    }

    fun moveFavorite(index: Int, delta: Int) {
        val moved = Customization.move(favoriteKeys.value, index, delta) ?: return
        viewModelScope.launch { customization.saveFavorites(moved) }
    }

    fun removeFavorite(index: Int) {
        val current = favoriteKeys.value
        if (index !in current.indices) return
        val updated = current.filterIndexed { i, _ -> i != index }
        viewModelScope.launch { customization.saveFavorites(updated) }
    }

    // The name is already cleaned by the caller (see Customization.cleanName).
    fun rename(app: LaunchableApp, name: String) {
        val updated = renames.value + (app.key to name)
        viewModelScope.launch { customization.saveRenames(updated) }
    }

    fun resetName(app: LaunchableApp) {
        val updated = renames.value - app.key
        viewModelScope.launch { customization.saveRenames(updated) }
    }

    // Silent clean-up (ROADMAP jalon 2): drops favourites and names of apps no longer installed.
    // Reads the stored data directly, so a not-yet-loaded state can never erase it.
    private suspend fun cleanUp(loaded: List<LaunchableApp>) {
        val installed = loaded.map { it.key }.toSet()

        val favorites = customization.favorites.first()
        val cleanFavorites = favorites.filter { it == Customization.SPACE_KEY || it in installed }
        if (cleanFavorites != favorites) customization.saveFavorites(cleanFavorites)

        val names = customization.renames.first()
        val cleanNames = names.filterKeys { it in installed }
        if (cleanNames != names) customization.saveRenames(cleanNames)
    }

    private fun LaunchableApp.withName(names: Map<String, String>): LaunchableApp =
        names[key]?.let { copy(label = it) } ?: this

    companion object {
        fun factory(repository: AppRepository, customization: CustomizationStore) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    AppsViewModel(repository, customization) as T
            }
    }
}
