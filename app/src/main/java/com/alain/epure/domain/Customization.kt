package com.alain.epure.domain

// Rules for favourites and custom names (SPEC F-2.1, F-2.4). Pure logic, covered by unit tests.
object Customization {

    const val MAX_FAVORITES = 7
    const val MAX_NAME_LENGTH = 30

    fun canAddFavorite(currentCount: Int): Boolean = currentCount < MAX_FAVORITES

    // Trims the name and cuts it to the maximum length. Null when nothing is left: an empty name is refused.
    fun cleanName(input: String): String? =
        input.trim().take(MAX_NAME_LENGTH).takeIf { it.isNotEmpty() }
}
