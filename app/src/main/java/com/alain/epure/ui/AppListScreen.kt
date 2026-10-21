package com.alain.epure.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.alain.epure.R
import com.alain.epure.domain.LaunchableApp
import com.alain.epure.ui.theme.AppNameStyle
import com.alain.epure.ui.theme.LabelStyle
import com.alain.epure.ui.theme.TextPrimary
import com.alain.epure.ui.theme.TextSecondary

@Composable
fun AppListScreen(
    viewModel: AppsViewModel,
    onOpenSettings: () -> Unit,
    onLongPress: (LaunchableApp) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // The search field is focused and the keyboard opens as soon as the list appears.
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        BasicTextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            textStyle = AppNameStyle.copy(color = TextPrimary),
            singleLine = true,
            cursorBrush = SolidColor(TextPrimary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { results.firstOrNull()?.let { viewModel.launch(it) } }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .focusRequester(focusRequester),
            decorationBox = { innerTextField ->
                Column {
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_hint),
                                style = AppNameStyle,
                                color = TextSecondary
                            )
                        }
                        innerTextField()
                    }
                    HorizontalDivider(color = TextSecondary, thickness = 1.dp)
                }
            }
        )

        if (query.isNotBlank() && results.isEmpty()) {
            Text(
                text = stringResource(R.string.no_app_found),
                style = AppNameStyle,
                color = TextSecondary,
                modifier = Modifier.padding(top = 24.dp)
            )
        }

        LazyColumn(Modifier.weight(1f)) {
            items(results, key = { it.key }) { app ->
                Text(
                    text = app.label,
                    style = AppNameStyle,
                    color = TextPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .combinedClickable(
                            onClick = { viewModel.launch(app) },
                            onLongClick = { onLongPress(app) }
                        )
                        .padding(vertical = 4.dp)
                )
            }
        }

        Text(
            text = stringResource(R.string.settings_open),
            style = LabelStyle,
            color = TextSecondary,
            modifier = Modifier
                .align(Alignment.End)
                .clickable(onClick = onOpenSettings)
                .padding(16.dp)
        )
    }
}
