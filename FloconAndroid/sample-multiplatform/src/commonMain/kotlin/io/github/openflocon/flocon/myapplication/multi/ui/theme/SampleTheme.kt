package io.github.openflocon.flocon.myapplication.multi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Same tokens as the Flocon Desktop dark theme (FloconColors.kt), so the sample looks like the app
 * it is debugged with. One color per plugin category.
 */
@Immutable
class SampleColors(
    val bg: Color = Color(0xFF111111),
    val surface: Color = Color(0xFF151515),
    val surfaceHigh: Color = Color(0xFF1F1F1F),
    val surfaceHighest: Color = Color(0xFF282828),
    val line: Color = Color(0xFF2A2A2A),
    val ink: Color = Color(0xFFECECEC),
    val inkMuted: Color = Color(0xFFA3A3A3),
    val inkFaint: Color = Color(0xFF828282),
    val accent: Color = Color(0xFFF5B440),
    val onAccent: Color = Color(0xFF1C1405),
    val success: Color = Color(0xFF4FD08A),
    val warning: Color = Color(0xFFF7A531),
    val error: Color = Color(0xFFFF6D6D),
)

/** The color of each family of plugins, used for its icon and its tile. */
enum class SampleCategory(val color: Color) {
    Network(Color(0xFF5EAAF7)),
    Database(Color(0xFF3FCFB5)),
    Analytics(Color(0xFFB494FF)),
    Accessibility(Color(0xFF6FD3E8)),
    FeatureFlags(Color(0xFFF5B440)),
    Table(Color(0xFFA9D65E)),
    Deeplink(Color(0xFFF483C2)),
    Crash(Color(0xFFFF6D6D)),
    Neutral(Color(0xFFA8ADB6)),
    ;

    /** Translucent background behind the icon. */
    val soft: Color get() = color.copy(alpha = 0.13f)
}

val LocalSampleColors = staticCompositionLocalOf { SampleColors() }

object SampleTheme {
    val colors: SampleColors
        @Composable get() = LocalSampleColors.current
}

@Composable
fun SampleTheme(content: @Composable () -> Unit) {
    val colors = SampleColors()
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = colors.accent,
            onPrimary = colors.onAccent,
            background = colors.bg,
            onBackground = colors.ink,
            surface = colors.surface,
            onSurface = colors.ink,
            surfaceVariant = colors.surfaceHigh,
            onSurfaceVariant = colors.inkMuted,
            outline = colors.line,
            error = colors.error,
        ),
    ) {
        CompositionLocalProvider(LocalSampleColors provides colors, content = content)
    }
}
