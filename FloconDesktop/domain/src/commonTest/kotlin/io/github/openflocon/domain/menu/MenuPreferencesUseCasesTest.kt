package io.github.openflocon.domain.menu

import io.github.openflocon.domain.menu.repository.MenuPreferencesRepository
import io.github.openflocon.domain.menu.usecase.ObserveMenuPreferencesUseCase
import io.github.openflocon.domain.menu.usecase.SetStartScreenUseCase
import io.github.openflocon.domain.menu.usecase.ToggleCollapsedMenuSectionUseCase
import io.github.openflocon.domain.menu.usecase.ToggleHiddenMenuItemUseCase
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

        override val startScreen = MutableStateFlow<String?>(null)

        override suspend fun setStartScreen(screenId: String?) {
            startScreen.value = screenId
        }

        override val hiddenItems = MutableStateFlow<Set<String>>(emptySet())

        override suspend fun setHiddenItems(items: Set<String>) {
            hiddenItems.value = items
        }

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
    fun `an item can be hidden then displayed again`() = runBlocking {
        val toggle = ToggleHiddenMenuItemUseCase(repository)

        toggle("Files")
        toggle("Tables")
        assertEquals(setOf("Files", "Tables"), repository.hiddenItems.value)

        toggle("Files")
        assertEquals(setOf("Tables"), repository.hiddenItems.value)
    }

    @Test
    fun `the start screen can be chosen then reset to the default`() = runBlocking {
        val setStartScreen = SetStartScreenUseCase(repository)

        setStartScreen("Network")
        assertEquals("Network", repository.startScreen.value)

        setStartScreen(null)
        assertEquals(null, repository.startScreen.value)
    }

    @Test
    fun `preferences combine the pins, the collapsed sections, the hidden items and the start screen`() = runBlocking {
        TogglePinnedMenuItemUseCase(repository)("Files")
        ToggleCollapsedMenuSectionUseCase(repository)("data")
        ToggleHiddenMenuItemUseCase(repository)("Tables")
        SetStartScreenUseCase(repository)("Network")

        val preferences = ObserveMenuPreferencesUseCase(repository)().first()

        assertEquals(listOf("Files"), preferences.pinnedItems)
        assertEquals(setOf("data"), preferences.collapsedSections)
        assertEquals(setOf("Tables"), preferences.hiddenItems)
        assertEquals("Network", preferences.startScreen)
    }
}
