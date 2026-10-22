package com.alain.epure.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alain.epure.R
import com.alain.epure.system.SystemSettings
import com.alain.epure.ui.theme.AppNameStyle
import com.alain.epure.ui.theme.LabelStyle
import com.alain.epure.ui.theme.TextPrimary
import com.alain.epure.ui.theme.TextSecondary

private const val REPO_URL = "https://github.com/beuchee/epure-launcher"
private const val REPO_NAME = "epure-launcher"

@Composable
fun SettingsScreen(
    onOpenFavorites: () -> Unit,
    onOpenRenamed: () -> Unit,
    onOpenShortcuts: () -> Unit,
    onOpenStats: () -> Unit
) {
    val context = LocalContext.current
    val version = remember { versionName(context) }
    val repoLabel = buildAnnotatedString {
        withStyle(SpanStyle(fontSize = 20.sp)) {
            append(stringResource(R.string.about_repo) + " : " + REPO_NAME)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = AppNameStyle,
            color = TextPrimary
        )
        ActionRow(
            text = AnnotatedString(stringResource(R.string.settings_favorites)),
            onClick = onOpenFavorites,
            modifier = Modifier.padding(top = 24.dp)
        )
        ActionRow(
            text = AnnotatedString(stringResource(R.string.settings_renamed)),
            onClick = onOpenRenamed
        )
        ActionRow(
            text = AnnotatedString(stringResource(R.string.settings_shortcuts)),
            onClick = onOpenShortcuts
        )
        ActionRow(
            text = AnnotatedString(stringResource(R.string.settings_stats)),
            onClick = onOpenStats
        )
        ActionRow(
            text = AnnotatedString(stringResource(R.string.quit_epure)),
            onClick = { SystemSettings.openHomeSettings(context) }
        )

        Text(
            text = stringResource(R.string.about_title),
            style = AppNameStyle,
            color = TextPrimary,
            modifier = Modifier.padding(top = 32.dp)
        )
        Text(
            text = stringResource(R.string.app_name),
            style = LabelStyle,
            color = TextPrimary,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = stringResource(R.string.about_version, version),
            style = LabelStyle,
            color = TextSecondary
        )
        Text(
            text = stringResource(R.string.about_license),
            style = LabelStyle,
            color = TextSecondary
        )
        ActionRow(
            text = repoLabel,
            onClick = { SystemSettings.openUrl(context, REPO_URL) }
        )
    }
}

private fun versionName(context: Context): String =
    context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
