package com.alain.epure.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.alain.epure.ui.theme.Black

enum class Screen { HOME, APPS, SETTINGS }

@Composable
fun EpureApp(viewModel: AppsViewModel, screen: Screen, onScreenChange: (Screen) -> Unit) {
    val favorites by viewModel.favorites.collectAsState()

    LaunchedEffect(screen) {
        if (screen != Screen.APPS) viewModel.clearQuery()
    }

    // Back goes one level up: settings -> list -> home. On home it does nothing (Épure stays the launcher).
    BackHandler(enabled = screen == Screen.APPS) { onScreenChange(Screen.HOME) }
    BackHandler(enabled = screen == Screen.SETTINGS) { onScreenChange(Screen.APPS) }
    BackHandler(enabled = screen == Screen.HOME) { }

    Box(
        Modifier
            .fillMaxSize()
            .background(Black)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        when (screen) {
            Screen.HOME -> HomeScreen(
                favorites = favorites,
                onOpenList = { onScreenChange(Screen.APPS) },
                onLaunch = viewModel::launch
            )
            Screen.APPS -> AppListScreen(
                viewModel = viewModel,
                onOpenSettings = { onScreenChange(Screen.SETTINGS) }
            )
            Screen.SETTINGS -> SettingsScreen()
        }
    }
}
