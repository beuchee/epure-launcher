package com.alain.epure.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class AppSearchTest {

    private fun app(label: String) = LaunchableApp(label, "pkg.$label", "Main")

    private val apps = listOf(
        app("Éclair"),
        app("Calendrier"),
        app("Chrome"),
        app("Galerie photo"),
        app("Horloge")
    )

    @Test
    fun accentsAreIgnored() {
        assertEquals(listOf("Éclair"), AppSearch.filter(apps, "eclair").map { it.label })
    }

    @Test
    fun caseIsIgnored() {
        assertEquals(listOf("Chrome"), AppSearch.filter(apps, "CHR").map { it.label })
    }

    @Test
    fun wordStartComesBeforeSubstring() {
        val result = AppSearch.filter(listOf(app("Photomaton"), app("Omar")), "oma")
        assertEquals(listOf("Omar", "Photomaton"), result.map { it.label })
    }

    @Test
    fun emptyQueryReturnsAllAppsAlphabetically() {
        assertEquals(
            listOf("Calendrier", "Chrome", "Éclair", "Galerie photo", "Horloge"),
            AppSearch.filter(apps, "   ").map { it.label }
        )
    }

    @Test
    fun noMatchReturnsEmptyList() {
        assertEquals(emptyList<LaunchableApp>(), AppSearch.filter(apps, "zzz"))
    }

    @Test
    fun sortIgnoresCaseAndAccents() {
        val result = AppSearch.sorted(listOf(app("zèbre"), app("Abeille"), app("éclair")))
        assertEquals(listOf("Abeille", "éclair", "zèbre"), result.map { it.label })
    }
}
