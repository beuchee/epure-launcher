package com.alain.epure.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.alain.epure.R
import com.alain.epure.domain.LaunchableApp
import com.alain.epure.domain.Shortcut
import com.alain.epure.domain.ShortcutChoice
import com.alain.epure.ui.theme.AppNameStyle
import com.alain.epure.ui.theme.Black
import com.alain.epure.ui.theme.LabelStyle
import com.alain.epure.ui.theme.TextPrimary
import com.alain.epure.ui.theme.TextSecondary

// Paramètres > Raccourcis: each shortcut opens its default action, an app, or nothing (F-2.2).
@Composable
fun ShortcutsScreen(viewModel: AppsViewModel) {
    val choices by viewModel.shortcutChoices.collectAsState()
    var picking by remember { mutableStateOf<Shortcut?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.shortcuts_title),
            style = AppNameStyle,
            color = TextPrimary
        )
        Shortcut.entries.forEach { shortcut ->
            ActionRow(
                text = AnnotatedString(
                    stringResource(shortcutName(shortcut)) + " : " + choiceLabel(choices[shortcut])
                ),
                onClick = { picking = shortcut },
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

    picking?.let { shortcut ->
        ShortcutPicker(
            viewModel = viewModel,
            shortcut = shortcut,
            onChoose = { value ->
                viewModel.setShortcut(shortcut, value)
                picking = null
            },
            onDismiss = { picking = null }
        )
    }
}

@Composable
private fun choiceLabel(choice: ShortcutChoice?): String = when (choice) {
    is ShortcutChoice.App -> choice.app.label
    ShortcutChoice.Off -> stringResource(R.string.shortcut_off)
    else -> stringResource(R.string.shortcut_default)
}

// Lets the user pick the default action, nothing, or one installed app.
@Composable
private fun ShortcutPicker(
    viewModel: AppsViewModel,
    shortcut: Shortcut,
    onChoose: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val apps by viewModel.results.collectAsState()
    val shape = RoundedCornerShape(16.dp)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth(0.9f)
                .background(Black, shape)
                .border(1.dp, TextSecondary, shape)
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = stringResource(shortcutName(shortcut)),
                style = LabelStyle,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            ActionRow(
                text = AnnotatedString(stringResource(R.string.shortcut_default)),
                onClick = { onChoose("default") },
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            ActionRow(
                text = AnnotatedString(stringResource(R.string.shortcut_off)),
                onClick = { onChoose("off") },
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            HorizontalDivider(color = TextSecondary, modifier = Modifier.padding(horizontal = 24.dp))
            LazyColumn(Modifier.heightIn(max = 420.dp)) {
                items(apps, key = { it.key }) { app: LaunchableApp ->
                    ActionRow(
                        text = AnnotatedString(app.label),
                        onClick = { onChoose(app.key) },
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }
    }
}

private fun shortcutName(shortcut: Shortcut): Int = when (shortcut) {
    Shortcut.CAMERA -> R.string.shortcut_camera
    Shortcut.PHONE -> R.string.shortcut_phone
    Shortcut.CLOCK -> R.string.shortcut_clock
}
