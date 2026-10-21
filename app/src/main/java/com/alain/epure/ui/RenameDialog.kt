package com.alain.epure.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.alain.epure.R
import com.alain.epure.domain.Customization
import com.alain.epure.domain.LaunchableApp
import com.alain.epure.ui.theme.AppNameStyle
import com.alain.epure.ui.theme.Black
import com.alain.epure.ui.theme.LabelStyle
import com.alain.epure.ui.theme.TextPrimary
import com.alain.epure.ui.theme.TextSecondary

// Rename dialog (F-2.4): a blank name is refused, the reset restores the system name.
@Composable
fun RenameDialog(
    app: LaunchableApp,
    onSave: (String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    // The current name is selected, so the first keystroke replaces it.
    var field by remember {
        mutableStateOf(TextFieldValue(app.label, TextRange(0, app.label.length)))
    }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val shape = RoundedCornerShape(16.dp)

    val save: () -> Unit = {
        Customization.cleanName(field.text)?.let(onSave)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth(0.9f)
                .background(Black, shape)
                .border(1.dp, TextSecondary, shape)
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.rename_title),
                style = LabelStyle,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            BasicTextField(
                value = field,
                onValueChange = { if (it.text.length <= Customization.MAX_NAME_LENGTH) field = it },
                textStyle = AppNameStyle.copy(color = TextPrimary),
                singleLine = true,
                cursorBrush = SolidColor(TextPrimary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { save() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .focusRequester(focusRequester),
                decorationBox = { innerTextField ->
                    Column {
                        innerTextField()
                        HorizontalDivider(color = TextSecondary, thickness = 1.dp)
                    }
                }
            )
            HorizontalDivider(
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            ActionRow(
                text = AnnotatedString(stringResource(R.string.rename_save)),
                onClick = save,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            ActionRow(
                text = AnnotatedString(stringResource(R.string.rename_reset)),
                onClick = onReset,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            ActionRow(
                text = AnnotatedString(stringResource(R.string.rename_cancel)),
                onClick = onDismiss,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}
