package com.alain.epure.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomizationTest {

    @Test
    fun favouritesAreLimitedToSeven() {
        assertTrue(Customization.canAddFavorite(6))
        assertFalse(Customization.canAddFavorite(7))
    }

    @Test
    fun nameIsTrimmed() {
        assertEquals("Mail", Customization.cleanName("  Mail  "))
    }

    @Test
    fun blankNameIsRefused() {
        assertNull(Customization.cleanName("   "))
        assertNull(Customization.cleanName(""))
    }

    @Test
    fun nameIsCutToThirtyCharacters() {
        val long = "a".repeat(40)
        assertEquals(30, Customization.cleanName(long)?.length)
    }

    @Test
    fun moveDownSwapsWithNext() {
        assertEquals(listOf("b", "a", "c"), Customization.move(listOf("a", "b", "c"), 0, 1))
    }

    @Test
    fun moveUpSwapsWithPrevious() {
        assertEquals(listOf("a", "c", "b"), Customization.move(listOf("a", "b", "c"), 2, -1))
    }

    @Test
    fun moveOutOfListIsRefused() {
        assertNull(Customization.move(listOf("a", "b"), 0, -1))
        assertNull(Customization.move(listOf("a", "b"), 1, 1))
        assertNull(Customization.move(listOf("a", "b"), 5, 1))
    }

    @Test
    fun spacesMoveLikeApps() {
        val keys = listOf("a/x", Customization.SPACE_KEY, "b/y")
        assertEquals(listOf("a/x", "b/y", Customization.SPACE_KEY), Customization.move(keys, 1, 1))
    }
}
