package com.alain.epure.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alain.epure.R
import com.alain.epure.domain.LaunchableApp
import com.alain.epure.system.SystemSettings
import com.alain.epure.ui.theme.AppNameStyle
import com.alain.epure.ui.theme.LabelStyle
import com.alain.epure.ui.theme.TextPrimary
import com.alain.epure.ui.theme.TextSecondary

// Paramètres > Statistiques: today's time and openings per app (F-3.2, DEC-0016).
@Composable
fun StatsScreen(viewModel: StatsViewModel, apps: List<LaunchableApp>, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    when (val current = state) {
        StatsState.Checking -> Unit
        StatsState.NoAccess -> PermissionScreen(
            title = stringResource(R.string.usage_access_title),
            why = stringResource(R.string.usage_access_why),
            notDone = stringResource(R.string.usage_access_not),
            steps = stringResource(R.string.usage_access_steps),
            onOpenSettings = { SystemSettings.openUsageAccess(context) },
            onLater = onBack
        )
        is StatsState.Loaded -> UsageList(current, apps, onManageAccess = { SystemSettings.openUsageAccess(context) })
    }
}

@Composable
private fun UsageList(loaded: StatsState.Loaded, apps: List<LaunchableApp>, onManageAccess: () -> Unit) {
    val labels = apps.associate { it.packageName to it.label }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.stats_title),
            style = AppNameStyle,
            color = TextPrimary
        )
        if (loaded.usages.isEmpty()) {
            Text(
                text = stringResource(R.string.stats_empty),
                style = LabelStyle,
                color = TextSecondary,
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            Text(
                text = stringResource(R.string.stats_total, durationText(loaded.totalMillis)),
                style = LabelStyle,
                color = TextSecondary,
                modifier = Modifier.padding(top = 16.dp)
            )
            loaded.usages.forEach { usage ->
                val opens = pluralStringResource(R.plurals.stats_opens, usage.opens, usage.opens)
                Column(Modifier.padding(top = 16.dp)) {
                    Text(
                        text = labels[usage.packageName] ?: usage.packageName,
                        style = LabelStyle,
                        color = TextPrimary
                    )
                    Text(
                        text = "${durationText(usage.totalMillis)} · $opens",
                        style = LabelStyle,
                        color = TextSecondary
                    )
                }
            }
        }
        // Opens the same system screen as the permission screen, to switch the access off (F-3.1).
        Spacer(Modifier.height(32.dp))
        TextAction(stringResource(R.string.stats_manage_access), onManageAccess)
    }
}

@Composable
private fun durationText(millis: Long): String {
    val minutes = millis / 60_000L
    return when {
        minutes == 0L -> stringResource(R.string.duration_under_minute)
        minutes < 60L -> stringResource(R.string.duration_minutes, minutes)
        else -> stringResource(R.string.duration_hours_minutes, minutes / 60, minutes % 60)
    }
}
