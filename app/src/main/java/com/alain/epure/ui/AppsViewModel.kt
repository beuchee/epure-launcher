package com.alain.epure.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alain.epure.data.AppRepository
import com.alain.epure.domain.AppSearch
import com.alain.epure.domain.LaunchableApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class AppsViewModel(private val repository: AppRepository) : ViewModel() {

    private val apps = MutableStateFlow<List<LaunchableApp>>(emptyList())
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val results: StateFlow<List<LaunchableApp>> =
        combine(apps, _query) { list, text -> AppSearch.filter(list, text) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

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

    companion object {
        fun factory(repository: AppRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AppsViewModel(repository) as T
        }
    }
}
