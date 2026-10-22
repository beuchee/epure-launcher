package com.alain.epure

import android.app.role.RoleManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.alain.epure.data.AppRepository
import com.alain.epure.data.CustomizationStore
import com.alain.epure.data.SettingsStore
import com.alain.epure.system.UsageAccess
import com.alain.epure.ui.AppsViewModel
import com.alain.epure.ui.EpureApp
import com.alain.epure.ui.Screen
import com.alain.epure.ui.StatsViewModel
import com.alain.epure.ui.theme.EpureTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val homeRoleRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    private val settings by lazy { SettingsStore(this) }
    private val appsViewModel: AppsViewModel by viewModels {
        AppsViewModel.factory(AppRepository(applicationContext), CustomizationStore(applicationContext))
    }
    private val statsViewModel: StatsViewModel by viewModels {
        StatsViewModel.factory(UsageAccess(applicationContext))
    }
    private var screen by mutableStateOf(Screen.HOME)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EpureTheme {
                EpureApp(
                    viewModel = appsViewModel,
                    statsViewModel = statsViewModel,
                    screen = screen,
                    onScreenChange = { screen = it }
                )
            }
        }
        if (savedInstanceState == null) {
            lifecycleScope.launch {
                if (!settings.isHomeRolePrompted()) {
                    settings.markHomeRolePrompted()
                    requestHomeRoleIfNeeded()
                }
            }
        }
    }

    // Home button while Épure is shown: back to a clean home screen (F-1.1).
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        screen = Screen.HOME
    }

    // Refreshes the list when Épure comes back, so installs and removals show up (F-1.2).
    override fun onResume() {
        super.onResume()
        appsViewModel.refresh()
        // Back from the usage settings: the statistics are read again if they are on screen (F-3.1).
        if (screen == Screen.STATS) statsViewModel.refresh()
    }

    // Asks Android to make Épure the home app, unless it already is.
    private fun requestHomeRoleIfNeeded() {
        val roleManager = getSystemService(RoleManager::class.java)
        if (roleManager.isRoleAvailable(RoleManager.ROLE_HOME) && !roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
            homeRoleRequest.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME))
        }
    }
}
