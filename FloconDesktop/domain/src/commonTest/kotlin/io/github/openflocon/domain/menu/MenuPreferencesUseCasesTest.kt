package io.github.openflocon.domain.menu

import io.github.openflocon.domain.menu.repository.MenuPreferencesRepository
import io.github.openflocon.domain.menu.usecase.ObserveMenuPreferencesUseCase
import io.github.openflocon.domain.menu.usecase.ToggleCollapsedMenuSectionUseCase
import io.github.openflocon.domain.menu.usecase.TogglePinnedMenuItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class MenuPreferencesUseCasesTest {

    private class FakeMenuPreferencesRepository : MenuPreferencesRepository {
        override val pinnedItems = MutableStateFlow<List<String>>(emptyList())
        override val collapsedSections = MutableStateFlow<Set<String>>(emptySet())

        override suspend fun setPinnedItems(items: List<String>) {
            pinnedItems.value = items
        }

        override suspend fun setCollapsedSections(sections: Set<String>) {
            collapsedSections.value = sections
        }
    }

    private val repository = FakeMenuPreferencesRepository()

    @Test
    fun `pinning keeps the order and unpinning removes the item`() = runBlocking {
        val toggle = TogglePinnedMenuItemUseCase(repository)

        toggle("Network")
        toggle("Database")
        assertEquals(listOf("Network", "Database"), repository.pinnedItems.value)

        toggle("Network")
        assertEquals(listOf("Database"), repository.pinnedItems.value)
    }

    @Test
    fun `a section can be collapsed then expanded`() = runBlocking {
        val toggle = ToggleCollapsedMenuSectionUseCase(repository)

        toggle("storage")
        assertEquals(setOf("storage"), repository.collapsedSections.value)

        toggle("storage")
        assertEquals(emptySet(), repository.collapsedSections.value)
    }

    @Test
    fun `preferences combine the pins and the collapsed sections`() = runBlocking {
        TogglePinnedMenuItemUseCase(repository)("Files")
        ToggleCollapsedMenuSectionUseCase(repository)("data")

        val preferences = ObserveMenuPreferencesUseCase(repository)().first()

        assertEquals(listOf("Files"), preferences.pinnedItems)
        assertEquals(setOf("data"), preferences.collapsedSections)
    }
}
