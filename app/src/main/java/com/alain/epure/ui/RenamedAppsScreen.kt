package com.alain.epure.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alain.epure.R
import com.alain.epure.domain.LaunchableApp
import com.alain.epure.ui.theme.AppNameStyle
import com.alain.epure.ui.theme.LabelStyle
import com.alain.epure.ui.theme.TextPrimary
import com.alain.epure.ui.theme.TextSecondary

// Paramètres > Apps renommées: shows the original name, lets the user edit or reset it (F-2.4).
@Composable
fun RenamedAppsScreen(viewModel: AppsViewModel) {
    val entries by viewModel.renamedApps.collectAsState()
    var renaming by remember { mutableStateOf<LaunchableApp?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.renamed_title),
            style = AppNameStyle,
            color = TextPrimary
        )
        if (entries.isEmpty()) {
            Text(
                text = stringResource(R.string.renamed_none),
                style = LabelStyle,
                color = TextSecondary,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
        entries.forEach { entry ->
            Column(Modifier.padding(top = 24.dp)) {
                Text(
                    text = entry.customName,
                    style = AppNameStyle,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(R.string.renamed_original, entry.app.label),
                    style = LabelStyle,
                    color = TextSecondary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    TextAction(stringResource(R.string.renamed_edit)) {
                        renaming = entry.app.copy(label = entry.customName)
                    }
                    TextAction(stringResource(R.string.renamed_reset)) {
                        viewModel.resetName(entry.app)
                    }
                }
            }
        }
    }

    renaming?.let { app ->
        RenameDialog(
            app = app,
            onSave = { name ->
                viewModel.rename(app, name)
                renaming = null
            },
            onReset = {
                viewModel.resetName(app)
                renaming = null
            },
            onDismiss = { renaming = null }
        )
    }
}
