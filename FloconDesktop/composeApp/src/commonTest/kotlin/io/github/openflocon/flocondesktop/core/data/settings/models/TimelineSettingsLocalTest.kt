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
}
