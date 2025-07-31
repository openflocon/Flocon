package io.github.openflocon.flocondesktop.common.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object FloconColors {
    val primary = Color(0xff0A84FF) // Your main accent color (Apple Blue)
    val onPrimary = Color(0xffF2F2F7) // Text/icons on primary

    val surface = Color(0xFF2C2C2E) // For cards, sheets, distinct elements
    val onSurface = Color(0xffF2F2F7) // Primary text on surfaces

    val background = Color(0xff1E1E1E) // Your main app background
    val onBackground = Color(0xffF2F2F7) // Primary text on background

    val pannel = Color(0xff1E1F22)

    val secondary = Color(0xffAEAEB2) // For secondary UI elements, less prominent text
    val onSecondary = Color(0xff1E1E1E) // Dark text/icons on secondary

    val tertiary = Color(0xFFBF5AF2) // An optional third accent (soft purple)
    val onTertiary = Color(0xffF2F2F7) // Text/icons on tertiary

    val error = Color(0xFFCF6679) // Standard error color
    val onError = Color(0xFF000000) // Text/icons on error color
}

val FloconColorScheme =
    darkColorScheme(
        primary = FloconColors.primary,
        onPrimary = FloconColors.onPrimary,
        primaryContainer = FloconColors.primary.copy(alpha = 0.2f),
        onPrimaryContainer = FloconColors.onPrimary,
        secondary = FloconColors.secondary,
        onSecondary = FloconColors.onSecondary,
        secondaryContainer = FloconColors.secondary.copy(alpha = 0.2f),
        onSecondaryContainer = FloconColors.onSecondary,
        tertiary = FloconColors.tertiary,
        onTertiary = FloconColors.onTertiary,
        tertiaryContainer = FloconColors.tertiary.copy(alpha = 0.2f),
        onTertiaryContainer = FloconColors.onTertiary,
        background = FloconColors.background,
        onBackground = FloconColors.onBackground,
        surface = FloconColors.surface,
        onSurface = FloconColors.onSurface,
        surfaceVariant = FloconColors.surface.copy(alpha = 0.8f),
        onSurfaceVariant = FloconColors.onSurface,
        error = FloconColors.error,
        onError = FloconColors.onError,
        errorContainer = Color(0xFFF2B8B5), // These specific error container colors are hardcoded Material 3 defaults
        onErrorContainer = Color(0xFF601410),
        outline = FloconColors.secondary.copy(alpha = 0.5f),
    )

@Composable
fun FloconTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FloconColorScheme,
        typography = MaterialTheme.typography, // Or define your custom typography,
        content = content,
    )
}
