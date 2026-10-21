package com.alain.epure.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.alain.epure.R
import com.alain.epure.domain.LaunchableApp
import com.alain.epure.ui.theme.AppNameStyle
import com.alain.epure.ui.theme.Black
import com.alain.epure.ui.theme.TextPrimary
import com.alain.epure.ui.theme.TextSecondary

// Long-press menu on an app (T-2). Only « Infos de l'app » is shown at jalon 1 (DEC-0009).
@Composable
fun AppActionsMenu(app: LaunchableApp, onDismiss: () -> Unit, onOpenDetails: () -> Unit) {
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
                text = app.label,
                style = AppNameStyle,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            HorizontalDivider(
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            ActionRow(
                text = AnnotatedString(stringResource(R.string.action_app_info)),
                onClick = onOpenDetails,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}
