package io.github.openflocon.domain.settings

import io.github.openflocon.domain.models.settings.AppScaleSetting
import kotlin.test.Test
import kotlin.test.assertEquals

class AppScaleSettingTest {

    @Test
    fun formerMultipliersLandOnTheirPreset() {
        assertEquals(AppScaleSetting.Compact, AppScaleSetting.fromLegacyMultiplier(0.9f))
        assertEquals(AppScaleSetting.Default, AppScaleSetting.fromLegacyMultiplier(1f))
        assertEquals(AppScaleSetting.Comfortable, AppScaleSetting.fromLegacyMultiplier(1.1f))
        assertEquals(AppScaleSetting.Large, AppScaleSetting.fromLegacyMultiplier(1.25f))
        assertEquals(AppScaleSetting.ExtraLarge, AppScaleSetting.fromLegacyMultiplier(1.5f))
        assertEquals(AppScaleSetting.Maximum, AppScaleSetting.fromLegacyMultiplier(2f))
    }

    @Test
    fun anyOtherMultiplierLandsOnTheClosestPreset() {
        assertEquals(AppScaleSetting.Compact, AppScaleSetting.fromLegacyMultiplier(0.5f))
        assertEquals(AppScaleSetting.Large, AppScaleSetting.fromLegacyMultiplier(1.3f))
        assertEquals(AppScaleSetting.Maximum, AppScaleSetting.fromLegacyMultiplier(3f))
    }
}
