package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import io.github.openflocon.library.designsystem.theme.FloconDarkColors
import io.github.openflocon.library.designsystem.theme.FloconLightColors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class LogMapperTest {

    @Test
    fun `fixedTypesGetDedicatedIconCustomGetsDefault`() {
        assertEquals(TimelineLogIcon.Navigation, logIconOf("navigation"))
        assertEquals(TimelineLogIcon.UserAction, logIconOf("userAction"))
        assertEquals(TimelineLogIcon.Lifecycle, logIconOf("lifecycle"))
        assertEquals(TimelineLogIcon.Custom, logIconOf("checkout"))
        assertEquals(TimelineLogIcon.Custom, logIconOf(""))
    }

    @Test
    fun `warningAndErrorLevelsGetDistinctColors`() {
        listOf(FloconDarkColors, FloconLightColors).forEach { colors ->
            val byLevel = TimelineLogLevel.entries.associateWith { it.color(colors) }

            assertEquals(TimelineLogLevel.entries.size, byLevel.values.toSet().size, "every level has its own color")
            assertNotEquals(byLevel.getValue(TimelineLogLevel.Warning), byLevel.getValue(TimelineLogLevel.Error))
            assertEquals(colors.statusWarning, byLevel.getValue(TimelineLogLevel.Warning))
            assertEquals(colors.statusError, byLevel.getValue(TimelineLogLevel.Error))
        }
    }
}
