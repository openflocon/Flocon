package io.github.openflocon.library.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class FloconSpacing(
    val none: Dp = 0.dp,
    val tiny: Dp = 2.dp,
    val extraSmall: Dp = 4.dp,
    val compact: Dp = 6.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 24.dp,
    val doubleExtraLarge: Dp = 32.dp,
    val tripleExtraLarge: Dp = 48.dp,
    val huge: Dp = 64.dp
)

fun FloconSpacing.scaledBy(multiplier: Float): FloconSpacing = if (multiplier == 1f) {
    this
} else {
    FloconSpacing(
        none = none * multiplier,
        tiny = tiny * multiplier,
        extraSmall = extraSmall * multiplier,
        compact = compact * multiplier,
        small = small * multiplier,
        medium = medium * multiplier,
        large = large * multiplier,
        extraLarge = extraLarge * multiplier,
        doubleExtraLarge = doubleExtraLarge * multiplier,
        tripleExtraLarge = tripleExtraLarge * multiplier,
        huge = huge * multiplier,
    )
}

val LocalFloconSpacing = staticCompositionLocalOf { FloconSpacing() }
