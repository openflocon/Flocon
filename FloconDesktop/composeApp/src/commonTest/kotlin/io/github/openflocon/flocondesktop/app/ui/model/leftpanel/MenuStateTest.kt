package io.github.openflocon.flocondesktop.app.ui.model.leftpanel

import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MenuStateTest {

    private fun MenuState.screens() = sections.flatMap { section -> section.items.map { it.screen } }

    @Test
    fun `everything is displayed when the plugins are unknown`() {
        val menu = buildMenu().applyPreferences(
            installedPlugins = null,
            pinnedIds = emptyList(),
            collapsedSectionIds = emptySet(),
        )

        assertTrue(SubScreen.Network in menu.screens())
        assertTrue(SubScreen.Files in menu.screens())
        assertTrue(SubScreen.CrashReporter in menu.screens())
        assertTrue(menu.pinnedItems.isEmpty())
    }

    @Test
    fun `the items of the plugins that are not installed are hidden`() {
        val menu = buildMenu().applyPreferences(
            installedPlugins = setOf("network", "database"),
            pinnedIds = emptyList(),
            collapsedSectionIds = emptySet(),
        )

        assertEquals(
            listOf(SubScreen.Network, SubScreen.Images, SubScreen.Database, SubScreen.AdbCommander),
            menu.screens(),
        )
        // always displayed
        assertEquals(listOf(SubScreen.Settings), menu.bottomItems.map { it.screen })
    }

    @Test
    fun `pinned items move to the top, in the pin order, and leave their section`() {
        val menu = buildMenu().applyPreferences(
            installedPlugins = null,
            pinnedIds = listOf("Database", "Network"),
            collapsedSectionIds = emptySet(),
        )

        assertEquals(listOf(SubScreen.Database, SubScreen.Network), menu.pinnedItems.map { it.screen })
        assertTrue(menu.pinnedItems.all { it.isPinned })
        assertTrue(SubScreen.Database !in menu.screens())
        assertTrue(SubScreen.Network !in menu.screens())
    }

    @Test
    fun `a pinned item that is not installed is not displayed`() {
        val menu = buildMenu().applyPreferences(
            installedPlugins = setOf("network"),
            pinnedIds = listOf("Files"),
            collapsedSectionIds = emptySet(),
        )

        assertTrue(menu.pinnedItems.isEmpty())
    }

    @Test
    fun `sections without items are removed and collapsed ones are flagged`() {
        val menu = buildMenu().applyPreferences(
            installedPlugins = setOf("network"),
            pinnedIds = emptyList(),
            collapsedSectionIds = setOf("network"),
        )

        // storage and data have no installed plugin
        assertEquals(listOf("network", "actions"), menu.sections.map { it.id })
        assertTrue(menu.sections.first { it.id == "network" }.isCollapsed)
    }

    @Test
    fun `the feature flags are displayed when the plugin is installed`() {
        val menu = buildMenu().applyPreferences(
            installedPlugins = setOf("feature_flags"),
            pinnedIds = emptyList(),
            collapsedSectionIds = emptySet(),
        )

        assertEquals(listOf(SubScreen.FeatureFlags, SubScreen.AdbCommander), menu.screens())
    }

    @Test
    fun `the items hidden by the user are not displayed, even when pinned`() {
        val menu = buildMenu().applyPreferences(
            installedPlugins = null,
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
    fun `the timeline is at the top, outside of any section, whatever the installed plugins`() {
        val menu = buildMenu().applyPreferences(
            installedPlugins = emptySet(),
            pinnedIds = listOf("Timeline"),
            collapsedSectionIds = emptySet(),
        )

        assertEquals(listOf(SubScreen.Timeline), menu.topItems.map { it.screen })
        assertTrue(menu.topItems.none { it.isPinnable })
        // it can't be pinned, so it is never duplicated
        assertTrue(menu.pinnedItems.isEmpty())
        assertEquals(listOf(SubScreen.AdbCommander), menu.screens())
    }
}
