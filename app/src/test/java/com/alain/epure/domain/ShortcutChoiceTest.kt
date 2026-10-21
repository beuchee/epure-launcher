package com.alain.epure.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ShortcutChoiceTest {

    private val apps = listOf(LaunchableApp("Mail", "com.mail", "Main"))

    @Test
    fun missingValueMeansDefault() {
        assertEquals(ShortcutChoice.Default, shortcutChoiceFrom(null, apps))
        assertEquals(ShortcutChoice.Default, shortcutChoiceFrom("default", apps))
    }

    @Test
    fun offDisablesTheShortcut() {
        assertEquals(ShortcutChoice.Off, shortcutChoiceFrom("off", apps))
    }

    @Test
    fun knownAppKeyIsUsed() {
        assertEquals(ShortcutChoice.App(apps[0]), shortcutChoiceFrom("com.mail/Main", apps))
    }

    @Test
    fun uninstalledAppFallsBackToDefault() {
        assertEquals(ShortcutChoice.Default, shortcutChoiceFrom("com.gone/Main", apps))
    }
}
