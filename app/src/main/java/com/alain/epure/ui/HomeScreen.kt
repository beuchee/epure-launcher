package com.alain.epure.ui

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alain.epure.R
import com.alain.epure.domain.FavoriteSlot
import com.alain.epure.domain.LaunchableApp
import com.alain.epure.domain.Shortcut
import com.alain.epure.domain.ShortcutChoice
import com.alain.epure.ui.theme.AppNameStyle
import com.alain.epure.ui.theme.ClockStyle
import com.alain.epure.ui.theme.DateStyle
import com.alain.epure.ui.theme.LabelStyle
import com.alain.epure.ui.theme.TextPrimary
import com.alain.epure.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    favorites: List<FavoriteSlot>,
    shortcuts: Map<Shortcut, ShortcutChoice>,
    onOpenList: () -> Unit,
    onLaunch: (LaunchableApp) -> Unit,
    onLongPress: (LaunchableApp) -> Unit,
    onShortcut: (Shortcut) -> Unit
) {
    val swipeThreshold = with(LocalDensity.current) { 100.dp.toPx() }

    Column(
        Modifier
            .fillMaxSize()
            .pointerInput(onOpenList) {
                // Right-to-left swipe opens the app list (DEC-0014).
                var total = 0f
                detectHorizontalDragGestures(
                    onDragStart = { total = 0f },
                    onHorizontalDrag = { _, dragAmount -> total += dragAmount },
                    onDragEnd = { if (total < -swipeThreshold) onOpenList() }
                )
            }
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Clock()
        Spacer(Modifier.height(32.dp))
        if (favorites.isEmpty()) {
            Text(
                text = stringResource(R.string.favorites_empty),
                style = LabelStyle,
                color = TextSecondary
            )
        } else {
            favorites.forEach { slot ->
                val app = slot.app
                if (app == null) {
                    // An empty space between favourites (F-2.3).
                    Spacer(Modifier.height(48.dp))
                } else {
                    Text(
                        text = app.label,
                        style = AppNameStyle,
                        color = TextPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .combinedClickable(
                                onClick = { onLaunch(app) },
                                onLongClick = { onLongPress(app) }
                            )
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Shortcuts(shortcuts = shortcuts, onShortcut = onShortcut)
    }
}

// The three shortcut icons; a disabled shortcut is not shown (F-2.2, DEC-0013).
@Composable
private fun Shortcuts(shortcuts: Map<Shortcut, ShortcutChoice>, onShortcut: (Shortcut) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Shortcut.entries.forEach { shortcut ->
            if (shortcuts[shortcut] != ShortcutChoice.Off) {
                val (icon, name) = when (shortcut) {
                    Shortcut.CAMERA -> R.drawable.ic_shortcut_camera to R.string.shortcut_camera
                    Shortcut.PHONE -> R.drawable.ic_shortcut_phone to R.string.shortcut_phone
                    Shortcut.CLOCK -> R.drawable.ic_shortcut_clock to R.string.shortcut_clock
                }
                Icon(
                    painter = painterResource(icon),
                    contentDescription = stringResource(name),
                    tint = TextPrimary,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(12.dp)
                        .clickable { onShortcut(shortcut) }
                )
            }
        }
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
