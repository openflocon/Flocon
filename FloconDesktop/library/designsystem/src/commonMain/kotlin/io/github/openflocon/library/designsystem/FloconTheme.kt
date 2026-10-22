@file:OptIn(ExperimentalComposeUiApi::class)

package io.github.openflocon.library.designsystem

import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.library.designsystem.components.FloconMenuRepresentation
import io.github.openflocon.library.designsystem.theme.FloconColors
import io.github.openflocon.library.designsystem.theme.FloconGlassBackground
import io.github.openflocon.library.designsystem.theme.FloconGlassDarkColors
import io.github.openflocon.library.designsystem.theme.FloconGlassLightColors
import io.github.openflocon.library.designsystem.theme.LocalFloconColors
import io.github.openflocon.library.designsystem.theme.AppScale
import io.github.openflocon.library.designsystem.theme.FloconSizes
import io.github.openflocon.library.designsystem.theme.LocalFloconSizes
import io.github.openflocon.library.designsystem.theme.FloconShape
import io.github.openflocon.library.designsystem.theme.FloconSpacing
import io.github.openflocon.library.designsystem.theme.LocalFloconShape
import io.github.openflocon.library.designsystem.theme.LocalFloconSpacing
import io.github.openflocon.library.designsystem.theme.materialDarkScheme
import io.github.openflocon.library.designsystem.theme.materialLightScheme
import io.github.openflocon.library.designsystem.theme.multiplyFontSizeBy

object FloconTheme {

    // the tokens of the design system (bg, ink, categories, http statuses…)
    val colors: FloconColors
        @Composable @ReadOnlyComposable
        get() = LocalFloconColors.current

    val typography: Typography
        @Composable @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes: FloconShape
        @Composable @ReadOnlyComposable
        get() = LocalFloconShape.current

    val isDarkTheme: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalIsDarkTheme.current

    val spacing: FloconSpacing
        @Composable @ReadOnlyComposable
        get() = LocalFloconSpacing.current

    val sizes: FloconSizes
        @Composable @ReadOnlyComposable
        get() = LocalFloconSizes.current
}

private val LocalIsDarkTheme = staticCompositionLocalOf { true }

@Composable
fun FloconTheme(
    // provides the spacing, the sizes and the font scale of the preset chosen by the user
    appScale: AppScale = AppScale.Default,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme) FloconGlassDarkColors else FloconGlassLightColors
    val ripple = ripple(color = colors.accent)
    val selectionTextColor = TextSelectionColors(
        handleColor = colors.accent,
        backgroundColor = colors.accent.copy(alpha = 0.35f),
    )

    val materialTypo = MaterialTheme.typography
    val typography = remember(materialTypo) {
        materialTypo.copy(
            bodyMedium = materialTypo.bodyMedium.copy(
                fontSize = 13.sp // was 14
            ),
            bodySmall = materialTypo.bodySmall.copy(
                fontSize = 11.sp, // was 12
            ),
            labelSmall = materialTypo.labelSmall.copy(
                fontSize = 10.sp // was 11
            )
        )
    }
    val scrollbarStyle = LocalScrollbarStyle.current.copy(
        unhoverColor = colors.bg300,
        hoverColor = colors.inkMuted
    )

    MaterialTheme(
        typography = typography.multiplyFontSizeBy(appScale.fontScale),
        colorScheme = if (isDarkTheme) {
            materialDarkScheme(colors)
        } else {
            materialLightScheme(colors)
        }
    ) {
        CompositionLocalProvider(
            LocalIndication provides ripple,
            LocalContentColor provides colors.ink,
            LocalIsDarkTheme provides isDarkTheme,
            LocalFloconColors provides colors,
            LocalFloconSpacing provides appScale.spacing,
            LocalFloconSizes provides appScale.sizes,
            LocalTextSelectionColors provides selectionTextColor,
            LocalScrollbarStyle provides scrollbarStyle,
            LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
            LocalContextMenuRepresentation provides FloconMenuRepresentation(),
            content = {
                FloconGlassBackground(isDarkTheme = isDarkTheme, content = content)
            }
        )
    }
}
