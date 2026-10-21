package com.alain.epure.domain

// One element of the favourites list: an app, or an empty space when app is null (F-2.3).
data class FavoriteSlot(val key: String, val app: LaunchableApp?)
