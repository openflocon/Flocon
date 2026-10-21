package io.github.openflocon.library.designsystem.theme

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppScaleTest {

    @Test
    fun defaultPresetProvidesTheDefaultTokens() {
        assertEquals(FloconSpacing(), AppScale.Default.spacing)
        assertEquals(FloconSizes(), AppScale.Default.sizes)
        assertEquals(1f, AppScale.Default.fontScale)
    }

    @Test
    fun presetsGrowWithTheirPercent() {
        val presets = AppScale.entries.sortedBy { it.percent }
        presets.zipWithNext { smaller, bigger ->
            assertTrue(smaller.fontScale < bigger.fontScale, "${bigger.name} font")
            assertTrue(smaller.spacing.large <= bigger.spacing.large, "${bigger.name} spacing")
            assertTrue(smaller.sizes.rowHeight <= bigger.sizes.rowHeight, "${bigger.name} row height")
            assertTrue(smaller.sizes.sidePanelWidth <= bigger.sizes.sidePanelWidth, "${bigger.name} side panel")
        }
    }

    @Test
    fun spacingTokensStayOrderedInEveryPreset() {
        AppScale.entries.forEach { preset ->
            val s = preset.spacing
            assertTrue(s.none <= s.tiny && s.tiny <= s.extraSmall && s.extraSmall <= s.compact, preset.name)
            assertTrue(s.compact <= s.small && s.small <= s.medium && s.medium <= s.large, preset.name)
            assertTrue(s.large <= s.extraLarge && s.extraLarge <= s.doubleExtraLarge, preset.name)
        }
    }
}
