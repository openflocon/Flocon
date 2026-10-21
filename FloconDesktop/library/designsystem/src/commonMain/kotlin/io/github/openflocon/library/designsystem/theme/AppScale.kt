package io.github.openflocon.library.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.math.abs

// the scale applied to the typography and the spacing, for the sizes that hold text (ex: the width of a column of times)
val LocalFloconScale = staticCompositionLocalOf { 1f }

// The scale of the application: it only drives the typography and the spacing of the theme
enum class AppScale(val multiplier: Float, val label: String) {
    Compact(multiplier = 0.9f, label = "Compact"),
    Default(multiplier = 1f, label = "Default"),
    Comfortable(multiplier = 1.1f, label = "Comfortable"),
    Large(multiplier = 1.25f, label = "Large"),
    ExtraLarge(multiplier = 1.5f, label = "Extra large"),
    Maximum(multiplier = 2f, label = "Maximum");

    val percent: Int get() = (multiplier * 100).toInt()

    companion object {
        // the multiplier is stored as a float: any stored value (ex: from the former slider) lands on the closest preset
        fun closestTo(multiplier: Float): AppScale = entries.minBy { abs(it.multiplier - multiplier) }
    }
}
