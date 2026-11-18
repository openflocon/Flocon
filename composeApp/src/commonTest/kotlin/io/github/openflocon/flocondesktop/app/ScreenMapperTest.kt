package io.github.openflocon.flocondesktop.app

import io.github.openflocon.domain.screen.models.FloconScreen
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScreenMapperTest {

    // every screen of the menu: complete it when a section is added, so that the MCP can display it
    private val allSubScreens: List<SubScreen> = listOf(
        SubScreen.Dashboard,
        SubScreen.Network,
        SubScreen.Images,
        SubScreen.Database,
        SubScreen.Files,
        SubScreen.SharedPreferences,
        SubScreen.Analytics,
        SubScreen.FeatureFlags,
        SubScreen.Accessibility,
        SubScreen.Tables,
        SubScreen.Settings,
        SubScreen.Deeplinks,
        SubScreen.AdbCommander,
        SubScreen.CrashReporter,
        SubScreen.Timeline,
        SubScreen.WebView,
    )

    @Test
    fun `every FloconScreen maps to a different SubScreen`() {
        val mapped = FloconScreen.entries.map { it.toSubScreen() }

        assertEquals(FloconScreen.entries.size, mapped.toSet().size)
    }

    @Test
    fun `every SubScreen but Settings can be requested`() {
        val mapped = FloconScreen.entries.map { it.toSubScreen() }.toSet()

        assertEquals(allSubScreens.filterNot { it == SubScreen.Settings }.toSet(), mapped)
    }

    @Test
    fun `Settings is not reachable`() {
        assertFalse(FloconScreen.entries.any { it.toSubScreen() == SubScreen.Settings })
        assertTrue(SubScreen.Settings in allSubScreens)
    }
}
