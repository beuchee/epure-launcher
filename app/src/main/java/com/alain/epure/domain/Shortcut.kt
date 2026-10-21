package com.alain.epure.domain

// The three shortcuts at the bottom of the home screen (F-2.2).
enum class Shortcut { CAMERA, PHONE, CLOCK }

// What a shortcut opens: its default system action, nothing, or an installed app.
sealed interface ShortcutChoice {
    data object Default : ShortcutChoice
    data object Off : ShortcutChoice
    data class App(val app: LaunchableApp) : ShortcutChoice
}

// Stored values: "default", "off", or the key of an app. Unknown or uninstalled apps fall back to the default.
fun shortcutChoiceFrom(stored: String?, apps: List<LaunchableApp>): ShortcutChoice = when (stored) {
    null, "default" -> ShortcutChoice.Default
    "off" -> ShortcutChoice.Off
    else -> apps.find { it.key == stored }?.let { ShortcutChoice.App(it) } ?: ShortcutChoice.Default
}
