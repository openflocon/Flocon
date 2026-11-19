package io.github.openflocon.flocondesktop.app.ui.model.leftpanel

import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MenuStateTest {

    private fun MenuState.screens() = sections.flatMap { section -> section.items.map { it.screen } }

    @Test
    fun `every plugin is displayed`() {
        val menu = buildMenu().applyPreferences(
            pinnedIds = emptyList(),
            collapsedSectionIds = emptySet(),
        )

        assertTrue(SubScreen.Network in menu.screens())
        assertTrue(SubScreen.Files in menu.screens())
        assertTrue(SubScreen.CrashReporter in menu.screens())
        assertTrue(menu.pinnedItems.isEmpty())
    }

    @Test
    fun `pinned items move to the top, in the pin order, and leave their section`() {
        val menu = buildMenu().applyPreferences(
            pinnedIds = listOf("Database", "Network"),
            collapsedSectionIds = emptySet(),
        )

        assertEquals(listOf(SubScreen.Database, SubScreen.Network), menu.pinnedItems.map { it.screen })
        assertTrue(menu.pinnedItems.all { it.isPinned })
        assertTrue(SubScreen.Database !in menu.screens())
        assertTrue(SubScreen.Network !in menu.screens())
    }

    @Test
    fun `sections without items are removed and collapsed ones are flagged`() {
        val menu = buildMenu().applyPreferences(
            pinnedIds = emptyList(),
            collapsedSectionIds = setOf("network"),
            hiddenIds = setOf("Files", "Database", "SharedPreferences"),
        )

        assertEquals(listOf("network", "data", "actions"), menu.sections.map { it.id })
        assertTrue(menu.sections.first { it.id == "network" }.isCollapsed)
    }

    @Test
    fun `the items hidden by the user are not displayed, even when pinned`() {
        val menu = buildMenu().applyPreferences(
            pinnedIds = listOf("Files", "Network"),
            collapsedSectionIds = emptySet(),
            hiddenIds = setOf("Files", "Tables", "Timeline", "Settings"),
        )

        assertEquals(listOf(SubScreen.Network), menu.pinnedItems.map { it.screen })
        assertTrue(SubScreen.Files !in menu.screens())
        assertTrue(SubScreen.Tables !in menu.screens())
        // not hideable
        assertEquals(listOf(SubScreen.Timeline), menu.topItems.map { it.screen })
        assertEquals(listOf(SubScreen.Settings), menu.bottomItems.map { it.screen })
    }

    @Test
    fun `every plugin screen can be hidden but not the timeline nor the settings`() {
        val hideable = hideableMenuItems().map { it.screen }

        assertTrue(SubScreen.Network in hideable)
        assertTrue(SubScreen.AdbCommander in hideable)
        assertTrue(SubScreen.Timeline !in hideable)
        assertTrue(SubScreen.Settings !in hideable)
    }

    @Test
    fun `the start screen can be the timeline or a plugin that is not hidden`() {
        val candidates = startScreenCandidates(hiddenIds = setOf("Files")).map { it.screen }

        assertTrue(SubScreen.Timeline in candidates)
        assertTrue(SubScreen.Network in candidates)
        assertTrue(SubScreen.Files !in candidates)
        assertTrue(SubScreen.Settings !in candidates)
    }

    @Test
    fun `the timeline is at the top, outside of any section`() {
        val menu = buildMenu().applyPreferences(
            pinnedIds = listOf("Timeline"),
            collapsedSectionIds = emptySet(),
        )

        assertEquals(listOf(SubScreen.Timeline), menu.topItems.map { it.screen })
        assertTrue(menu.topItems.none { it.isPinnable })
        // it can't be pinned, so it is never duplicated
        assertTrue(menu.pinnedItems.isEmpty())
        assertTrue(SubScreen.Timeline !in menu.screens())
    }
}
