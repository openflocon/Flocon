package io.github.openflocon.flocondesktop.features.timeline.graph

import io.github.openflocon.flocondesktop.features.timeline.mapper.LogGlyphColor
import io.github.openflocon.library.designsystem.theme.FloconDarkColors
import io.github.openflocon.library.designsystem.theme.FloconLightColors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LabelContrastTest {

    @Test
    fun `every log marker color has a readable label in both palettes`() {
        assertEquals(7, LogGlyphColor.entries.size)
        mapOf("dark" to FloconDarkColors, "light" to FloconLightColors).forEach { (name, colors) ->
            LogGlyphColor.entries.forEach { token ->
                val background = token.color(colors)
                val ratio = contrastRatio(background, readableInkOn(background))
                assertTrue(ratio >= 4.5, "$name $token: contrast $ratio")
            }
        }
    }

    @Test
    fun `the ink with the higher contrast is picked`() {
        assertEquals(FloconLightColors.ink, readableInkOn(FloconDarkColors.logInfo))
        assertEquals(FloconLightColors.bg100, readableInkOn(FloconLightColors.logDebug))
    }
}
