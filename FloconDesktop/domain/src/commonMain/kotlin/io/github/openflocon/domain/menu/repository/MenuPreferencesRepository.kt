package io.github.openflocon.domain.menu.repository

import kotlinx.coroutines.flow.Flow

interface MenuPreferencesRepository {
    // ids of the pinned menu items, in the order they have been pinned
    val pinnedItems: Flow<List<String>>

    // ids of the collapsed menu sections
    val collapsedSections: Flow<Set<String>>

    suspend fun setPinnedItems(items: List<String>)
    suspend fun setCollapsedSections(sections: Set<String>)
}
