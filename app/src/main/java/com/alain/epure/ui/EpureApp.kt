package com.alain.epure.ui

import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.alain.epure.R
import com.alain.epure.domain.Customization
import com.alain.epure.domain.LaunchableApp
import com.alain.epure.ui.theme.Black

enum class Screen { HOME, APPS, SETTINGS, FAVORITES, RENAMED, SHORTCUTS }

@Composable
fun EpureApp(viewModel: AppsViewModel, screen: Screen, onScreenChange: (Screen) -> Unit) {
    val favorites by viewModel.favorites.collectAsState()
    val shortcuts by viewModel.shortcutChoices.collectAsState()
    val context = LocalContext.current
    val favoritesFull = stringResource(R.string.favorites_full, Customization.MAX_FAVORITES)

    // The long-press menu and the rename dialog are shared by the home screen and the list (T-2).
    var menuApp by remember { mutableStateOf<LaunchableApp?>(null) }
    var renamingApp by remember { mutableStateOf<LaunchableApp?>(null) }

    LaunchedEffect(screen) {
        if (screen != Screen.APPS) viewModel.clearQuery()
        menuApp = null
        renamingApp = null
    }

    // Back goes one level up: home <- list <- settings <- favourites or renamed apps.
    // On home it does nothing (Épure stays the launcher).
    BackHandler(enabled = screen == Screen.APPS) { onScreenChange(Screen.HOME) }
    BackHandler(enabled = screen == Screen.SETTINGS) { onScreenChange(Screen.APPS) }
    BackHandler(enabled = screen == Screen.FAVORITES || screen == Screen.RENAMED || screen == Screen.SHORTCUTS) {
        onScreenChange(Screen.SETTINGS)
    }
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
                shortcuts = shortcuts,
                onOpenList = { onScreenChange(Screen.APPS) },
                onLaunch = viewModel::launch,
                onLongPress = { menuApp = it },
                onShortcut = viewModel::launchShortcut
            )
            Screen.APPS -> AppListScreen(
                viewModel = viewModel,
                onOpenSettings = { onScreenChange(Screen.SETTINGS) },
                onLongPress = { menuApp = it }
            )
            Screen.SETTINGS -> SettingsScreen(
                onOpenFavorites = { onScreenChange(Screen.FAVORITES) },
                onOpenRenamed = { onScreenChange(Screen.RENAMED) },
                onOpenShortcuts = { onScreenChange(Screen.SHORTCUTS) }
            )
            Screen.FAVORITES -> FavoritesScreen(viewModel = viewModel)
            Screen.RENAMED -> RenamedAppsScreen(viewModel = viewModel)
            Screen.SHORTCUTS -> ShortcutsScreen(viewModel = viewModel)
        }
    }

    menuApp?.let { app ->
        AppActionsMenu(
            app = app,
            isFavorite = favorites.any { it.key == app.key },
            onDismiss = { menuApp = null },
            onOpenDetails = {
                viewModel.openAppDetails(app)
                menuApp = null
            },
            onToggleFavorite = {
                if (!viewModel.toggleFavorite(app)) {
                    Toast.makeText(context, favoritesFull, Toast.LENGTH_SHORT).show()
                }
                menuApp = null
            },
            onRename = {
                renamingApp = app
                menuApp = null
            }
        )
    }

    renamingApp?.let { app ->
        RenameDialog(
            app = app,
            onSave = { name ->
                viewModel.rename(app, name)
                renamingApp = null
            },
            onReset = {
                viewModel.resetName(app)
                renamingApp = null
            },
            onDismiss = { renamingApp = null }
        )
    }
}
