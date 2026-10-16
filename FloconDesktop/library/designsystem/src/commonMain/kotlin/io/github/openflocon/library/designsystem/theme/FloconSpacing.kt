package io.github.openflocon.library.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class FloconSpacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 24.dp,
    val doubleExtraLarge: Dp = 32.dp,
    val tripleExtraLarge: Dp = 48.dp,
    val huge: Dp = 64.dp
)

val LocalFloconSpacing = staticCompositionLocalOf { FloconSpacing() }
