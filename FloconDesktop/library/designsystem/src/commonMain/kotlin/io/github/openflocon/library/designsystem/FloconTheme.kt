@file:OptIn(ExperimentalComposeUiApi::class)

package io.github.openflocon.library.designsystem

import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
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
import io.github.openflocon.library.designsystem.theme.FloconColorPaletteNew
import io.github.openflocon.library.designsystem.theme.FloconColors
import io.github.openflocon.library.designsystem.theme.FloconDarkColors
import io.github.openflocon.library.designsystem.theme.FloconLightColors
import io.github.openflocon.library.designsystem.theme.LocalFloconColors
import io.github.openflocon.library.designsystem.theme.LocalFloconScale
import io.github.openflocon.library.designsystem.theme.FloconShape
import io.github.openflocon.library.designsystem.theme.FloconSpacing
import io.github.openflocon.library.designsystem.theme.LocalFloconColorPalette
import io.github.openflocon.library.designsystem.theme.LocalFloconShape
import io.github.openflocon.library.designsystem.theme.LocalFloconSpacing
import io.github.openflocon.library.designsystem.theme.darkPalette
import io.github.openflocon.library.designsystem.theme.lightPalette
import io.github.openflocon.library.designsystem.theme.materialDarkScheme
import io.github.openflocon.library.designsystem.theme.materialLightScheme
import io.github.openflocon.library.designsystem.theme.multiplyFontSizeBy
import io.github.openflocon.library.designsystem.theme.scaledBy

object FloconTheme {

    val colorPalette: FloconColorPaletteNew
        @Composable @ReadOnlyComposable
        get() = LocalFloconColorPalette.current

    // the tokens of the design system (categories, http statuses…), used by the screens redesigned with it
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

    val scale: Float
        @Composable @ReadOnlyComposable
        get() = LocalFloconScale.current
}

private val LocalIsDarkTheme = staticCompositionLocalOf { true }

@Composable
fun FloconTheme(
    // scales the typography and the spacing
    scale: Float = 1f,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorPalette = when {
        isDarkTheme -> darkPalette
        else -> lightPalette
    }
    val colors = if (isDarkTheme) FloconDarkColors else FloconLightColors
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
        unhoverColor = colorPalette.secondary,
        hoverColor = colorPalette.onSecondary
    )

    MaterialTheme(
        typography = typography.multiplyFontSizeBy(scale),
        colorScheme = if (isDarkTheme) {
            materialDarkScheme(colorPalette)
        } else {
            materialLightScheme(colorPalette)
        }
    ) {
        CompositionLocalProvider(
            LocalIndication provides ripple,
            LocalIsDarkTheme provides isDarkTheme,
            LocalFloconColorPalette provides colorPalette,
            LocalFloconColors provides colors,
            LocalFloconSpacing provides remember(scale) { FloconSpacing().scaledBy(scale) },
            LocalFloconScale provides scale,
            LocalTextSelectionColors provides selectionTextColor,
            LocalScrollbarStyle provides scrollbarStyle,
            LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
            LocalContextMenuRepresentation provides FloconMenuRepresentation(),
            content = content
        )
    }
}
