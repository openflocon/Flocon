package io.github.openflocon.flocondesktop.core.data.settings.models

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnalyticsSettingsLocalTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `excluding a name with commas and quotes round-trips through json`() {
        val settings = AnalyticsSettingsLocal()
            .withExcluded("app.a", "x,y")
            .withExcluded("app.a", "say \"hi\"")

        val decoded = json.decodeFromString(
            AnalyticsSettingsLocal.serializer(),
            json.encodeToString(AnalyticsSettingsLocal.serializer(), settings),
        )

        assertEquals(setOf("x,y", "say \"hi\""), decoded.excluded("app.a"))
    }

    @Test
    fun `two packages are kept apart`() {
        val settings = AnalyticsSettingsLocal().withExcluded("app.a", "e")

        assertEquals(setOf("e"), settings.excluded("app.a"))
        assertTrue(settings.excluded("app.b").isEmpty())
    }

    @Test
    fun `restoring the last name drops the package key`() {
        val settings = AnalyticsSettingsLocal()
            .withExcluded("app.a", "e")
            .withRestored("app.a", "e")

        assertTrue(settings.excludedEventNamesByPackage.isEmpty())
    }

    @Test
    fun `restoring all clears only that package`() {
        val settings = AnalyticsSettingsLocal()
            .withExcluded("app.a", "e1")
            .withExcluded("app.a", "e2")
            .withExcluded("app.b", "e3")
            .withAllRestored("app.a")

        assertTrue(settings.excluded("app.a").isEmpty())
        assertEquals(setOf("e3"), settings.excluded("app.b"))
    }
}
