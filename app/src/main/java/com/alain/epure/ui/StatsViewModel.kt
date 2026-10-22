package com.alain.epure.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alain.epure.domain.AppUsage
import com.alain.epure.domain.UsageSummary
import com.alain.epure.system.UsageAccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface StatsState {
    // Nothing shown yet, while the permission is being checked.
    data object Checking : StatsState
    data object NoAccess : StatsState
    data class Loaded(val usages: List<AppUsage>, val totalMillis: Long) : StatsState
}

class StatsViewModel(private val usageAccess: UsageAccess) : ViewModel() {

    private val _state = MutableStateFlow<StatsState>(StatsState.Checking)
    val state: StateFlow<StatsState> = _state.asStateFlow()

    // Reads the usage data again each time it is asked for, so a permission granted in the settings applies at once.
    fun refresh() {
        viewModelScope.launch {
            _state.value = withContext(Dispatchers.IO) {
                if (!usageAccess.isGranted()) return@withContext StatsState.NoAccess
                val today = usageAccess.readToday()
                val usages = UsageSummary.build(today.totalMillis, today.opens, usageAccess.excludedPackages())
                StatsState.Loaded(usages, UsageSummary.totalMillis(usages))
            }
        }
    }

    companion object {
        fun factory(usageAccess: UsageAccess) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    StatsViewModel(usageAccess) as T
            }
    }
}
