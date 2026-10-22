package io.github.openflocon.library.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

/**
 * TODO Remove when all component are overriden
 */
internal fun materialDarkScheme(colors: FloconColors): ColorScheme = darkColorScheme(
    primary = colors.bg100,
    onPrimary = colors.ink,
    primaryContainer = colors.bg100.copy(alpha = 0.2f),
    onPrimaryContainer = colors.ink,
    secondary = colors.bg300,
    onSecondary = colors.inkMuted,
    secondaryContainer = colors.bg300.copy(alpha = 0.2f),
    onSecondaryContainer = colors.inkMuted,
    tertiary = colors.ink,
    onTertiary = colors.bg100,
    tertiaryContainer = colors.ink.copy(alpha = 0.2f),
    onTertiaryContainer = colors.bg100,
    surface = colors.bg000,
    onSurface = colors.ink,
    surfaceVariant = colors.bg000.copy(alpha = 0.8f),
    onSurfaceVariant = colors.ink,
    error = colors.statusError,
    onError = colors.accentInk,
    errorContainer = Color(0xFFF2B8B5), // These specific error container colors are hardcoded Material 3 defaults
    onErrorContainer = Color(0xFF601410),
    outline = colors.line,
)

internal fun materialLightScheme(colors: FloconColors): ColorScheme = lightColorScheme(
    primary = colors.bg100,
    onPrimary = colors.ink,
    primaryContainer = colors.bg100.copy(alpha = 0.2f),
    onPrimaryContainer = colors.ink,
    secondary = colors.bg300,
    onSecondary = colors.inkMuted,
    secondaryContainer = colors.bg300.copy(alpha = 0.2f),
    onSecondaryContainer = colors.inkMuted,
    tertiary = colors.ink,
    onTertiary = colors.bg100,
    tertiaryContainer = colors.ink.copy(alpha = 0.2f),
    onTertiaryContainer = colors.bg100,
    surface = colors.bg000,
    onSurface = colors.ink,
    surfaceVariant = colors.bg000.copy(alpha = 0.8f),
    onSurfaceVariant = colors.ink,
    error = colors.statusError,
    onError = colors.accentInk,
    errorContainer = Color(0xFFF2B8B5), // These specific error container colors are hardcoded Material 3 defaults
    onErrorContainer = Color(0xFF601410),
    outline = colors.line,
)

@Stable
fun FloconColors.contentColorFor(backgroundColor: Color) = when (backgroundColor) {
    bg000, bg100, bg200, bg300, floating -> ink
    accentSoft -> accent
    accent -> accentInk
    ink -> bg100
    statusError -> accentInk
    else -> Color.Unspecified
}
