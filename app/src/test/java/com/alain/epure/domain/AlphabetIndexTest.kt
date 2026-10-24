package com.alain.epure.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class AlphabetIndexTest {

    private fun app(label: String) = LaunchableApp(label, "pkg.$label", "Main")

    @Test
    fun accentedLettersAreGroupedWithTheirBaseLetter() {
        assertEquals('E', AlphabetIndex.letterOf("Éclair"))
    }

    @Test
    fun lowerCaseNamesAreUpperCased() {
        assertEquals('C', AlphabetIndex.letterOf("chrome"))
    }

    @Test
    fun digitsAndSymbolsAreGroupedUnderHash() {
        assertEquals(AlphabetIndex.OTHER, AlphabetIndex.letterOf("1Password"))
        assertEquals(AlphabetIndex.OTHER, AlphabetIndex.letterOf("-Test"))
    }

    @Test
    fun onlyLettersPresentAreListed() {
        val apps = AppSearch.sorted(listOf(app("Éclair"), app("Chrome"), app("Calendrier"), app("123")))
        assertEquals(listOf('#', 'C', 'E'), AlphabetIndex.letters(apps))
    }

    @Test
    fun firstIndexOfLetterIsTheFirstAppUnderIt() {
        val apps = AppSearch.sorted(listOf(app("Galerie"), app("Calendrier"), app("Chrome")))
        assertEquals(0, AlphabetIndex.firstIndexOf(apps, 'C'))
        assertEquals(2, AlphabetIndex.firstIndexOf(apps, 'G'))
    }

    @Test
    fun absentLetterGivesMinusOne() {
        val apps = AppSearch.sorted(listOf(app("Chrome")))
        assertEquals(-1, AlphabetIndex.firstIndexOf(apps, 'M'))
    }
}
