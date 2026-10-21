package com.alain.epure.domain

// Rules for favourites and custom names (SPEC F-2.1, F-2.3, F-2.4). Pure logic, covered by unit tests.
object Customization {

    const val MAX_FAVORITES = 7
    const val MAX_NAME_LENGTH = 30

    // Key of an empty space between favourites (F-2.3). App keys always contain "/".
    const val SPACE_KEY = "space"

    fun canAddFavorite(currentCount: Int): Boolean = currentCount < MAX_FAVORITES

    // Trims the name and cuts it to the maximum length. Null when nothing is left: an empty name is refused.
    fun cleanName(input: String): String? =
        input.trim().take(MAX_NAME_LENGTH).takeIf { it.isNotEmpty() }

    // Moves the item at index by delta (-1 up, +1 down). Null when the move is impossible.
    fun move(keys: List<String>, index: Int, delta: Int): List<String>? {
        val target = index + delta
        if (index !in keys.indices || target !in keys.indices) return null
        return keys.toMutableList().apply { add(target, removeAt(index)) }
    }
}
