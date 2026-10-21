package com.alain.epure.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alain.epure.data.AppRepository
import com.alain.epure.data.CustomizationStore
import com.alain.epure.domain.AppSearch
import com.alain.epure.domain.Customization
import com.alain.epure.domain.LaunchableApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppsViewModel(
    private val repository: AppRepository,
    private val customization: CustomizationStore
) : ViewModel() {

    private val apps = MutableStateFlow<List<LaunchableApp>>(emptyList())
    private val renames = customization.renames
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())
    private val favoriteKeys = customization.favorites
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // Apps as shown everywhere: the custom name replaces the system name (DEC-0010).
    val results: StateFlow<List<LaunchableApp>> =
        combine(apps, renames, _query) { list, names, text ->
            AppSearch.filter(list.map { it.withName(names) }, text)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Favourites in the order they were added. An uninstalled app simply disappears.
    val favorites: StateFlow<List<LaunchableApp>> =
        combine(apps, favoriteKeys, renames) { list, keys, names ->
            keys.mapNotNull { key -> list.find { it.key == key }?.withName(names) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun refresh() {
        apps.value = repository.loadApps()
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

    // The name is already cleaned by the caller (see Customization.cleanName).
    fun rename(app: LaunchableApp, name: String) {
        val updated = renames.value + (app.key to name)
        viewModelScope.launch { customization.saveRenames(updated) }
    }

    fun resetName(app: LaunchableApp) {
        val updated = renames.value - app.key
        viewModelScope.launch { customization.saveRenames(updated) }
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
