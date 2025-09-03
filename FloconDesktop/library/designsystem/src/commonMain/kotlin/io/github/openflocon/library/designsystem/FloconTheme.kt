package io.github.openflocon.library.designsystem

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.library.designsystem.theme.FloconColorPaletteNew
import io.github.openflocon.library.designsystem.theme.FloconShape
import io.github.openflocon.library.designsystem.theme.LocalFloconColorPalette
import io.github.openflocon.library.designsystem.theme.LocalFloconShape
import io.github.openflocon.library.designsystem.theme.darkPalette
import io.github.openflocon.library.designsystem.theme.lightPalette
import io.github.openflocon.library.designsystem.theme.materialDarkScheme
import io.github.openflocon.library.designsystem.theme.materialLightScheme

object FloconTheme {

    val colorPalette: FloconColorPaletteNew
        @Composable @ReadOnlyComposable get() = LocalFloconColorPalette.current

    val typography: Typography
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography

    val shapes: FloconShape
        @Composable @ReadOnlyComposable get() = LocalFloconShape.current

}

@Composable
fun FloconTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(), // TODO Add setting and override
    content: @Composable () -> Unit
) {
    val colorPalette = when {
        isDarkTheme -> darkPalette
        else -> lightPalette
    }
    val ripple = ripple(color = colorPalette.accent)
    val selectionTextColor = TextSelectionColors(
        handleColor = Color.White,
        backgroundColor = Color.White.copy(alpha = 0.5f)
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
        typography = typography,
        colorScheme = if (isDarkTheme) {
            materialDarkScheme(colorPalette)
        } else {
            materialLightScheme(colorPalette)
        }
    ) {
        CompositionLocalProvider(
            LocalIndication provides ripple,
            LocalFloconColorPalette provides colorPalette,
            LocalTextSelectionColors provides selectionTextColor,
            LocalScrollbarStyle provides scrollbarStyle,
            LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
            content = content
        )
    }
}
