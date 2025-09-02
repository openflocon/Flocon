package io.github.openflocon.library.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class FloconColorPaletteNew(
    val surface: Color, // Base color // 0xFF2C2C2E
    val onSurface: Color, // Base content color on surface // 0xffF2F2F7

    val primary: Color, // Color of container on the surface // 0xFF29282c
    val onPrimary: Color, // Color of content on the primary // 0xffF2F2F7

    val secondary: Color, // Color of container on the primary // 0xff1E1F22
    val onSecondary: Color, // Color of content on the secondary // Color.LightGray

    val tertiary: Color, // Color of container on the secondary // 0xFFe2e2e9
    val onTertiary: Color, // Color of content on the tertiary // 0xFF2e3036

    val accent: Color, // Color of container when actif // 0xFF4a330c
    val onAccent: Color, // Color of content when actif // 0xFFfcaf28

    val error: Color, // Color of container when in error // 0xFFCF6679
    val onError: Color, // Color of content when in error // 0xFF000000

    val exceptions: Color // Specific to exception status
)

internal val lightPalette = FloconColorPaletteNew(
    surface = Color(0xFF2C2C2E),
    onSurface = Color(0xffF2F2F7),
    primary = Color(0xFF29282c),
    onPrimary = Color(0xffF2F2F7),
    secondary = Color(0xff494c54),
    onSecondary = Color(0xFFbfc8e0),
    tertiary = Color(0xFFe2e2e9),
    onTertiary = Color(0xFF2e3036),
    accent = Color(0xFF4a330c),
    onAccent = Color(0xFFfcaf28),
    error = Color(0xFFCF6679),
    onError = Color(0xFF000000),
    exceptions = Color(0xFF7B1FA2)
)

internal val darkPalette = FloconColorPaletteNew(
    surface = Color(0xFF2C2C2E),
    onSurface = Color(0xffF2F2F7),
    primary = Color(0xFF1f1e21),
    onPrimary = Color(0xffF2F2F7),
    secondary = Color(0xff3a3c42),
    onSecondary = Color(0xFFbfc8e0),
    tertiary = Color(0xFFe2e2e9),
    onTertiary = Color(0xFF2e3036),
    accent = Color(0xFF4a330c),
    onAccent = Color(0xFFfcaf28),
    error = Color(0xFFCF6679),
    onError = Color(0xFF000000),
    exceptions = Color(0xFF7B1FA2)
)

//internal val lightPalette = FloconColorPalette(
//    primary = Color(0xff69696b),
//    onPrimary = Color(0xffF2F2F7),
//    primaryContainer = Color(0xff0A84FF),
//    onPrimaryContainer = Color(0xffF2F2F7),
//
//    surface = Color(0xFF2C2C2E),
//    onSurface = Color(0xffF2F2F7),
//    surfaceVariant = Color(0xFF29282c),
//    onSurfaceVariant = Color(0xffF2F2F7),
//    inverseSurface = Color(0xFFe2e2e9),
//    inverseOnSurface = Color(0xFF2e3036),
//
//    background = Color(0xff1E1E1E),
//    onBackground = Color(0xffF2F2F7),
//
//    panel = Color(0xff1E1F22),
//    onPanel = Color.LightGray, // TODO
//
//    secondary = Color(0xffAEAEB2),
//    onSecondary = Color(0xff1E1E1E),
//
//    tertiary = Color(0xFFBF5AF2),
//    onTertiary = Color(0xffF2F2F7),
//
//    error = Color(0xFFCF6679),
//    onError = Color(0xFF000000),
//
//    exceptions = Color(0xFF7B1FA2)
//)
//
//internal val darkPalette = FloconColorPalette(
//    primary = Color(0xff2a2a2b),
//    onPrimary = Color(0xffF2F2F7),
//    primaryContainer = Color(0xff0A84FF),
//    onPrimaryContainer = Color(0xffF2F2F7),
//
//    surface = Color(0xFF2C2C2E),
//    onSurface = Color(0xffF2F2F7),
//    surfaceVariant = Color(0xFF29282c),
//    onSurfaceVariant = Color(0xffF2F2F7),
//    inverseSurface = Color(0xFFe2e2e9),
//    inverseOnSurface = Color(0xFF2e3036),
//
//    background = Color(0xff1E1E1E),
//    onBackground = Color(0xffF2F2F7),
//
//    panel = Color(0xff1E1F22),
//    onPanel = Color.LightGray, // TODO
//
//    secondary = Color(0xFF4a330c),//Color(0xff0f3166),
//    onSecondary = Color(0xFFfcaf28),
//
//    tertiary = Color(0xFFBF5AF2),
//    onTertiary = Color(0xffF2F2F7),
//
//    error = Color(0xFFCF6679),
//    onError = Color(0xFF000000),
//
//    exceptions = Color(0xFF7B1FA2)
//)

/**
 * TODO Remove when all component are overriden
 */
internal fun materialDarkScheme(palette: FloconColorPaletteNew) = darkColorScheme(
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

internal fun materialLightScheme(palette: FloconColorPaletteNew) = lightColorScheme(
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
fun FloconColorPaletteNew.contentColorFor(backgroundColor: Color) = when (backgroundColor) {
    primary -> onPrimary
    secondary -> onSecondary
    tertiary -> onTertiary
    surface -> onSurface
    error -> onError
    accent -> onAccent
    else -> Color.Unspecified
}

@Stable
fun FloconColorPaletteNew.contentColorFor(backgroundColor: Color) = when (backgroundColor) {
    primary -> onPrimary
    secondary -> onSecondary
    tertiary -> onTertiary
    surface -> onSurface
    error -> onError
    accent -> onAccent
    else -> Color.Unspecified
}
