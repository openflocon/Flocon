package io.github.openflocon.domain.menu.repository

import kotlinx.coroutines.flow.Flow

interface MenuPreferencesRepository {
    // ids of the pinned menu items, in the order they have been pinned
    val pinnedItems: Flow<List<String>>

    // ids of the collapsed menu sections
    val collapsedSections: Flow<Set<String>>

    // ids of the menu items the user chose not to display
    val hiddenItems: Flow<Set<String>>

    // id of the screen displayed when the app starts, null: the default one (the timeline)
    val startScreen: Flow<String?>

    suspend fun setStartScreen(screenId: String?)
    suspend fun setPinnedItems(items: List<String>)
    suspend fun setCollapsedSections(sections: Set<String>)
    suspend fun setHiddenItems(items: Set<String>)
}
