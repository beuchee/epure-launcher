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
}
