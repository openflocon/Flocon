package io.github.openflocon.library.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The spacing scale of the design system, the only source of padding and gaps.
 * Each token matches a `--space-*` of the design: tiny = space-0 (2), extraSmall = space-1 (4),
 * compact = space-1-5 (6), small = space-2 (8), medium = space-3 (12), large = space-4 (16),
 * extraLarge = space-5 (24), doubleExtraLarge = space-6 (32).
 */
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
    )
}

val LocalFloconSpacing = staticCompositionLocalOf { FloconSpacing() }
