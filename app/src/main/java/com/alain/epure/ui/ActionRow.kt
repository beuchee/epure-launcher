package com.alain.epure.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.alain.epure.ui.theme.AppNameStyle
import com.alain.epure.ui.theme.TextPrimary

// One text action in a list: at least 48 dp tall to stay easy to tap (SPEC § 9).
@Composable
fun ActionRow(text: AnnotatedString, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = AppNameStyle,
        color = TextPrimary,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    )
}
