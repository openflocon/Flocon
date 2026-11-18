package io.github.openflocon.flocondesktop.features.timeline.mapper

import androidx.compose.ui.graphics.Color
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import io.github.openflocon.library.designsystem.theme.FloconColors
import io.github.openflocon.library.designsystem.theme.FloconDarkColors
import io.github.openflocon.library.designsystem.theme.FloconLightColors
import kotlin.math.abs
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LogMapperTest {

    private fun levelColors(colors: FloconColors) = TimelineLogLevel.entries.associateWith { it.color(colors) }

    private fun luminance(color: Color): Double {
        fun channel(c: Float): Double = c.toDouble().let { if (it <= 0.03928) it / 12.92 else ((it + 0.055) / 1.055).pow(2.4) }
        return 0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)
    }

    @Test
    fun `reservedTypesGetTheirOwnShapeAndColorWhateverTheCase`() {
        listOf("navigation", "Navigation", "NAVIGATION").forEach {
            assertEquals(LogGlyph(LogGlyphShape.Arrow, LogGlyphColor.Navigation), logGlyphOf(it, TimelineLogLevel.Info))
        }
        listOf("userAction", "useraction", "USERACTION").forEach {
            assertEquals(LogGlyph(LogGlyphShape.Pointer, LogGlyphColor.UserAction), logGlyphOf(it, TimelineLogLevel.Info))
        }
        listOf("lifecycle", "Lifecycle").forEach {
            assertEquals(LogGlyph(LogGlyphShape.Heart, LogGlyphColor.Lifecycle), logGlyphOf(it, TimelineLogLevel.Info))
        }
        // the level of a typed log does not change its glyph
        assertEquals(logGlyphOf("navigation", TimelineLogLevel.Info), logGlyphOf("navigation", TimelineLogLevel.Error))
        assertTrue(logGlyphOf("lifecycle", null).isTyped)
    }

    @Test
    fun `anythingElseIsADiamondColoredByLevel`() {
        listOf("billing", "checkout", "", null).forEach { type ->
            assertEquals(LogGlyph(LogGlyphShape.Diamond, LogGlyphColor.Debug), logGlyphOf(type, TimelineLogLevel.Debug))
            assertEquals(LogGlyph(LogGlyphShape.Diamond, LogGlyphColor.Info), logGlyphOf(type, TimelineLogLevel.Info))
            assertEquals(LogGlyph(LogGlyphShape.Diamond, LogGlyphColor.Warning), logGlyphOf(type, TimelineLogLevel.Warning))
            assertEquals(LogGlyph(LogGlyphShape.Diamond, LogGlyphColor.Error), logGlyphOf(type, TimelineLogLevel.Error))
        }
        assertFalse(logGlyphOf("billing", TimelineLogLevel.Warning).isTyped)
        // no level counts as info
        assertEquals(LogGlyphColor.Info, logGlyphOf("billing", null).color)
    }

    @Test
    fun `glyphColorsComeFromTheDesignSystem`() {
        listOf(FloconDarkColors, FloconLightColors).forEach { colors ->
            assertEquals(colors.logNavigation, logGlyphOf("navigation", null).color(colors))
            assertEquals(colors.logUserAction, logGlyphOf("userAction", null).color(colors))
            assertEquals(colors.logLifecycle, logGlyphOf("lifecycle", null).color(colors))
            TimelineLogLevel.entries.forEach { level ->
                assertEquals(level.color(colors), logGlyphOf("billing", level).color(colors))
            }
        }
    }

    @Test
    fun `facetValuesMapToLevels`() {
        assertEquals(TimelineLogLevel.Warning, logLevelOf("WARNING"))
        assertEquals(TimelineLogLevel.Debug, logLevelOf("debug"))
        assertNull(logLevelOf("navigation"))
    }

    @Test
    fun `levelColorsArePinnedAndDistinctInBothPalettes`() {
        val dark = levelColors(FloconDarkColors)
        assertEquals(Color(0xFF3B6FE0), dark.getValue(TimelineLogLevel.Debug))
        assertEquals(Color(0xFFE3C2FF), dark.getValue(TimelineLogLevel.Info))
        assertEquals(Color(0xFFF7A531), dark.getValue(TimelineLogLevel.Warning))
        assertEquals(Color(0xFFFF6D6D), dark.getValue(TimelineLogLevel.Error))
        val light = levelColors(FloconLightColors)
        assertEquals(Color(0xFF1E40AF), light.getValue(TimelineLogLevel.Debug))
        assertEquals(Color(0xFFA855F7), light.getValue(TimelineLogLevel.Info))
        assertEquals(Color(0xFFA35A00), light.getValue(TimelineLogLevel.Warning))
        assertEquals(Color(0xFFC62F2F), light.getValue(TimelineLogLevel.Error))
        listOf(dark, light).forEach { byLevel ->
            assertEquals(4, TimelineLogLevel.entries.size)
            assertEquals(TimelineLogLevel.entries.size, byLevel.values.toSet().size, "every level has its own color")
        }
    }

    @Test
    fun `typeColorsArePinnedInBothPalettes`() {
        assertEquals(Color(0xFF4ADE80), FloconDarkColors.logNavigation)
        assertEquals(Color(0xFF67D4E8), FloconDarkColors.logUserAction)
        assertEquals(Color(0xFFFF5C7A), FloconDarkColors.logLifecycle)
        assertEquals(Color(0xFF15803D), FloconLightColors.logNavigation)
        assertEquals(Color(0xFF0E7490), FloconLightColors.logUserAction)
        assertEquals(Color(0xFFBE123C), FloconLightColors.logLifecycle)
    }

    @Test
    fun `debugAndInfoDifferStronglyInLightness`() {
        // the owner is colorblind: blue vs violet must be told apart by lightness. WCAG relative luminance: the gaps are 0.44 (dark) and 0.145 (light),
        // 0.12 keeps the pair apart (the blue and the violet of the light palette are 0.07 vs 0.22)
        listOf(FloconDarkColors, FloconLightColors).forEach { colors ->
            val gap = abs(luminance(TimelineLogLevel.Debug.color(colors)) - luminance(TimelineLogLevel.Info.color(colors)))
            assertTrue(gap > 0.12, "Debug and Info lightness gap is $gap")
        }
    }

    @Test
    fun `typedColorsAreNotLevelColors`() {
        listOf(FloconDarkColors, FloconLightColors).forEach { colors ->
            val level = levelColors(colors).values.toSet()
            listOf(colors.logNavigation, colors.logUserAction, colors.logLifecycle).forEach { assertFalse(it in level) }
            assertNotEquals(colors.statusInfo, TimelineLogLevel.Info.color(colors))
            assertNotEquals(colors.catNetwork, TimelineLogLevel.Debug.color(colors))
        }
    }
}
