package io.github.openflocon.domain.models.settings

import kotlin.math.abs

// How dense the application is: the design system maps each preset to its own spacing, sizes and font scale
enum class AppScaleSetting(private val legacyMultiplier: Float) {
    Compact(0.9f),
    Default(1f),
    Comfortable(1.1f),
    Large(1.25f),
    ExtraLarge(1.5f),
    Maximum(2f),
    ;

    companion object {
        val DEFAULT = Default

        // the former setting was a float multiplier: any stored value lands on the closest preset
        fun fromLegacyMultiplier(multiplier: Float): AppScaleSetting = entries.minBy { abs(it.legacyMultiplier - multiplier) }
    }
}
