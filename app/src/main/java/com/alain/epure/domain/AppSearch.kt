package com.alain.epure.domain

import java.text.Normalizer

data class LaunchableApp(
    val label: String,
    val packageName: String,
    val className: String
)

// Pure search and sort logic: no Android dependency, covered by unit tests.
object AppSearch {

    // Lower case, accents removed: "Éclair" becomes "eclair".
    fun normalize(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .lowercase()

    fun sorted(apps: List<LaunchableApp>): List<LaunchableApp> =
        apps.sortedBy { normalize(it.label) }

    // Apps whose name starts a word match come first, then the others; each group is alphabetical.
    fun filter(apps: List<LaunchableApp>, query: String): List<LaunchableApp> {
        val needle = normalize(query.trim())
        if (needle.isEmpty()) return sorted(apps)
        return apps
            .filter { normalize(it.label).contains(needle) }
            .sortedWith(
                compareBy<LaunchableApp> { if (startsWord(normalize(it.label), needle)) 0 else 1 }
                    .thenBy { normalize(it.label) }
            )
    }

    private fun startsWord(name: String, needle: String): Boolean =
        name.split(' ', '-', '\'', '’', '.', '_').any { it.startsWith(needle) }
}
