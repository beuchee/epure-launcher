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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alain.epure.R
import com.alain.epure.ui.theme.AppNameStyle
import com.alain.epure.ui.theme.LabelStyle
import com.alain.epure.ui.theme.TextPrimary
import com.alain.epure.ui.theme.TextSecondary

// Shared screen for a special permission (T-1): why, what Épure does not do, numbered steps, then the system setting.
@Composable
fun PermissionScreen(
    title: String,
    why: String,
    notDone: String,
    steps: String,
    onOpenSettings: () -> Unit,
    onLater: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = title,
            style = AppNameStyle,
            color = TextPrimary
        )
        Text(
            text = why,
            style = LabelStyle,
            color = TextPrimary,
            modifier = Modifier.padding(top = 24.dp)
        )
        Text(
            text = notDone,
            style = LabelStyle,
            color = TextSecondary,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = steps,
            style = LabelStyle,
            color = TextPrimary,
            modifier = Modifier.padding(top = 24.dp)
        )
        Spacer(Modifier.height(24.dp))
        TextAction(stringResource(R.string.permission_open_settings), onOpenSettings)
        TextAction(stringResource(R.string.permission_later), onLater)
        Text(
            text = stringResource(R.string.permission_revocable),
            style = LabelStyle,
            color = TextSecondary,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}
