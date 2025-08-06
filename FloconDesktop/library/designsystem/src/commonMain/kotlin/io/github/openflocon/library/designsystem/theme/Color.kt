package io.github.openflocon.library.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class FloconColorPalette(
    val primary: Color,
    val onPrimary: Color,

    val primaryContainer: Color,
    val onPrimaryContainer: Color,

    val surface: Color,
    val onSurface: Color,

    val surfaceVariant: Color,
    val onSurfaceVariant: Color,

    val inverseSurface: Color,
    val inverseOnSurface: Color,

    val background: Color,
    val onBackground: Color,

    val panel: Color,
    val onPanel: Color,

    val secondary: Color,
    val onSecondary: Color,

    val tertiary: Color,
    val onTertiary: Color,

    val error: Color,
    val onError: Color
)

internal val lightPalette = FloconColorPalette(
    primary = Color(0xff0A84FF),
    onPrimary = Color(0xffF2F2F7),
    primaryContainer = Color(0xff0A84FF),
    onPrimaryContainer = Color(0xffF2F2F7),

    surface = Color(0xFF2C2C2E),
    onSurface = Color(0xffF2F2F7),
    surfaceVariant = Color(0xFF2C2C2E).copy(alpha = .8f),
    onSurfaceVariant = Color(0xffF2F2F7),
    inverseSurface = Color(0xFFe2e2e9),
    inverseOnSurface = Color(0xFF2e3036),

    background = Color(0xff1E1E1E),
    onBackground = Color(0xffF2F2F7),

    panel = Color(0xff1E1F22),
    onPanel = Color.LightGray, // TODO

    secondary = Color(0xffAEAEB2),
    onSecondary = Color(0xff1E1E1E),

    tertiary = Color(0xFFBF5AF2),
    onTertiary = Color(0xffF2F2F7),

    error = Color(0xFFCF6679),
    onError = Color(0xFF000000)
)

internal val darkPalette = FloconColorPalette(
    primary = Color(0xff0A84FF),
    onPrimary = Color(0xffF2F2F7),
    primaryContainer = Color(0xff0A84FF),
    onPrimaryContainer = Color(0xffF2F2F7),

    surface = Color(0xFF2C2C2E),
    onSurface = Color(0xffF2F2F7),
    surfaceVariant = Color(0xFF2C2C2E).copy(alpha = .8f),
    onSurfaceVariant = Color(0xffF2F2F7),
    inverseSurface = Color(0xFFe2e2e9),
    inverseOnSurface = Color(0xFF2e3036),

    background = Color(0xff1E1E1E),
    onBackground = Color(0xffF2F2F7),

    panel = Color(0xff1E1F22),
    onPanel = Color.LightGray, // TODO

    secondary = Color(0xffAEAEB2),
    onSecondary = Color(0xff1E1E1E),

    tertiary = Color(0xFFBF5AF2),
    onTertiary = Color(0xffF2F2F7),

    error = Color(0xFFCF6679),
    onError = Color(0xFF000000)
)

/**
 * TODO Remove when all component are overriden
 */
internal fun materialDarkScheme(palette: FloconColorPalette) = darkColorScheme(
    primary = palette.primary,
    onPrimary = palette.onPrimary,
    primaryContainer = palette.primary.copy(alpha = 0.2f),
    onPrimaryContainer = palette.onPrimary,
    secondary = palette.secondary,
    onSecondary = palette.onSecondary,
    secondaryContainer = palette.secondary.copy(alpha = 0.2f),
    onSecondaryContainer = palette.onSecondary,
    tertiary = palette.tertiary,
    onTertiary = palette.onTertiary,
    tertiaryContainer = palette.tertiary.copy(alpha = 0.2f),
    onTertiaryContainer = palette.onTertiary,
    background = palette.background,
    onBackground = palette.onBackground,
    surface = palette.surface,
    onSurface = palette.onSurface,
    surfaceVariant = palette.surface.copy(alpha = 0.8f),
    onSurfaceVariant = palette.onSurface,
    error = palette.error,
    onError = palette.onError,
    errorContainer = Color(0xFFF2B8B5), // These specific error container colors are hardcoded Material 3 defaults
    onErrorContainer = Color(0xFF601410),
    outline = palette.secondary.copy(alpha = 0.5f),
)

internal fun materialLightScheme(palette: FloconColorPalette) = lightColorScheme(
    primary = palette.primary,
    onPrimary = palette.onPrimary,
    primaryContainer = palette.primary.copy(alpha = 0.2f),
    onPrimaryContainer = palette.onPrimary,
    secondary = palette.secondary,
    onSecondary = palette.onSecondary,
    secondaryContainer = palette.secondary.copy(alpha = 0.2f),
    onSecondaryContainer = palette.onSecondary,
    tertiary = palette.tertiary,
    onTertiary = palette.onTertiary,
    tertiaryContainer = palette.tertiary.copy(alpha = 0.2f),
    onTertiaryContainer = palette.onTertiary,
    background = palette.background,
    onBackground = palette.onBackground,
    surface = palette.surface,
    onSurface = palette.onSurface,
    surfaceVariant = palette.surface.copy(alpha = 0.8f),
    onSurfaceVariant = palette.onSurface,
    error = palette.error,
    onError = palette.onError,
    errorContainer = Color(0xFFF2B8B5), // These specific error container colors are hardcoded Material 3 defaults
    onErrorContainer = Color(0xFF601410),
    outline = palette.secondary.copy(alpha = 0.5f),
)

internal val LocalFloconColorPalette = staticCompositionLocalOf { lightPalette }

@Stable
fun FloconColorPalette.contentColorFor(backgroundColor: Color) = when (backgroundColor) {
    primary -> onPrimary
    secondary -> onSecondary
    tertiary -> onTertiary
    surface -> onSurface
    surfaceVariant -> onSurfaceVariant
    inverseSurface -> inverseOnSurface
    background -> onBackground
    error -> onError
    panel -> onPanel
    else -> Color.Unspecified
}
