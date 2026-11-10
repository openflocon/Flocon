package io.github.openflocon.flocondesktop.core.data.settings.models

import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class TimelineSettingsLocalTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `nothing disabled means every source is selected`() {
        assertEquals(TimelineSource.entries.toSet(), TimelineSettingsLocal().toDomain().selectedSources)
    }

    @Test
    fun `a disabled source is not selected`() {
        val settings = TimelineSettingsLocal(disabledSources = setOf("Database", "Crash")).toDomain()

        assertEquals(TimelineSource.entries.toSet() - TimelineSource.Database - TimelineSource.Crash, settings.selectedSources)
    }

    @Test
    fun `an unknown disabled name is ignored`() {
        val settings = TimelineSettingsLocal(disabledSources = setOf("Gone")).toDomain()

        assertEquals(TimelineSource.entries.toSet(), settings.selectedSources)
    }

    @Test
    fun `only the sources that are off are stored`() {
        val local = TimelineSettingsDomainModel(
            invertList = true,
            selectedSources = setOf(TimelineSource.Network, TimelineSource.Analytics),
        ).toLocal()

        assertEquals(TimelineSource.entries.map { it.name }.toSet() - "Network" - "Analytics", local.disabledSources)
        assertEquals(true, local.invertList)
    }

    @Test
    fun `the keys missing from the stored json get their default`() {
        val local = json.decodeFromString(TimelineSettingsLocal.serializer(), """{"auto_scroll":true}""")

        assertEquals(TimelineSettingsLocal(autoScroll = true), local)
    }

    @Test
    fun `a settings file without the screenshot keys loads as off and 5 s`() {
        val domain = json.decodeFromString(TimelineSettingsLocal.serializer(), """{}""").toDomain()
        val old = json.decodeFromString(TimelineSettingsLocal.serializer(), """{"graph":false}""").toDomain()

        assertEquals(false, domain.screenshots)
        assertEquals(5, domain.screenshotIntervalSeconds)
        assertEquals(false, old.screenshots)
        assertEquals(5, old.screenshotIntervalSeconds)
    }

    @Test
    fun `a stored interval that is not allowed loads as 5 s`() {
        assertEquals(5, TimelineSettingsLocal(screenshotIntervalSeconds = 7).toDomain().screenshotIntervalSeconds)
    }

    @Test
    fun `the screenshot options round trip through toLocal and toDomain`() {
        val domain = TimelineSettingsDomainModel(screenshots = true, screenshotIntervalSeconds = 30)

        assertEquals(domain, domain.toLocal().toDomain())
    }

    @Test
    fun `the single graph is off when missing from the stored json and round trips`() {
        assertEquals(false, TimelineSettingsLocal().toDomain().graphSingle)

        val domain = TimelineSettingsDomainModel(graphSingle = true)
        assertEquals(domain, domain.toLocal().toDomain())
    }
}
