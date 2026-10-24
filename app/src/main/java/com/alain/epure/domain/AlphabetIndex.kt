package com.alain.epure.domain

// Letters of the alphabetical index (F-1.6, DEC-0017). Pure logic, covered by unit tests.
object AlphabetIndex {

    // Digits, symbols and names without a Latin letter are grouped under this entry.
    const val OTHER = '#'

    // Accents are removed first: "Éclair" is listed under E.
    fun letterOf(label: String): Char {
        val first = AppSearch.normalize(label).firstOrNull()
        return if (first != null && first in 'a'..'z') first.uppercaseChar() else OTHER
    }

    // Only the letters present in the list, '#' first, then A to Z.
    fun letters(apps: List<LaunchableApp>): List<Char> =
        apps.map { letterOf(it.label) }.toSortedSet().toList()

    // Position of the first app under this letter, or -1 when no app starts with it.
    fun firstIndexOf(apps: List<LaunchableApp>, letter: Char): Int =
        apps.indexOfFirst { letterOf(it.label) == letter }
}
