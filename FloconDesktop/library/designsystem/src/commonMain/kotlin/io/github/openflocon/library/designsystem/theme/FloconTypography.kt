package io.github.openflocon.library.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

/**
 * The text styles of the design system, the only source of text styles in the app.
 * The first ones are the Material roles, scaled by the user's [AppScale]; the mono ones are for technical data
 * (times, http codes, methods, paths) so that digits and letters line up in columns.
 */
@Immutable
data class FloconTypography(
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
    val monoMedium: TextStyle,
    val monoSmall: TextStyle,
)

fun Typography.toFloconTypography() = FloconTypography(
    displayLarge = displayLarge,
    displayMedium = displayMedium,
    displaySmall = displaySmall,
    headlineLarge = headlineLarge,
    headlineMedium = headlineMedium,
    headlineSmall = headlineSmall,
    titleLarge = titleLarge,
    titleMedium = titleMedium,
    titleSmall = titleSmall,
    bodyLarge = bodyLarge,
    bodyMedium = bodyMedium,
    bodySmall = bodySmall,
    labelLarge = labelLarge,
    labelMedium = labelMedium,
    labelSmall = labelSmall,
    monoMedium = labelMedium.copy(fontFamily = FontFamily.Monospace),
    monoSmall = labelSmall.copy(fontFamily = FontFamily.Monospace),
)

val LocalFloconTypography = staticCompositionLocalOf { Typography().toFloconTypography() }
