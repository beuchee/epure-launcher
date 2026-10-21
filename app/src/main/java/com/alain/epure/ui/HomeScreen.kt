package com.alain.epure.ui

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alain.epure.R
import com.alain.epure.ui.theme.TextPrimary
import com.alain.epure.ui.theme.TextSecondary
import com.alain.epure.ui.theme.ClockStyle
import com.alain.epure.ui.theme.DateStyle
import com.alain.epure.ui.theme.LabelStyle
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(onOpenList: () -> Unit) {
    val swipeThreshold = with(LocalDensity.current) { 100.dp.toPx() }

    Column(
        Modifier
            .fillMaxSize()
            .pointerInput(onOpenList) {
                var total = 0f
                detectVerticalDragGestures(
                    onDragStart = { total = 0f },
                    onVerticalDrag = { _, dragAmount -> total += dragAmount },
                    onDragEnd = { if (total < -swipeThreshold) onOpenList() }
                )
            }
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Clock()
        Spacer(Modifier.weight(1f))
        Text(
            text = stringResource(R.string.home_apps),
            style = LabelStyle,
            color = TextSecondary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = onOpenList)
                .padding(16.dp)
        )
    }
}

// Updates every minute, only while the home screen is visible: no background timer.
@Composable
private fun Clock() {
    val is24Hour = DateFormat.is24HourFormat(LocalContext.current)
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(is24Hour) {
        while (true) {
            now = LocalDateTime.now()
            delay(60_000L - System.currentTimeMillis() % 60_000L)
        }
    }

    val time = now.format(DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm"))
    val date = now.format(DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.getDefault()))

    Text(
        text = time,
        style = ClockStyle,
        color = TextPrimary,
        maxLines = 1,
        softWrap = false
    )
    Text(text = date, style = DateStyle, color = TextSecondary)
}
