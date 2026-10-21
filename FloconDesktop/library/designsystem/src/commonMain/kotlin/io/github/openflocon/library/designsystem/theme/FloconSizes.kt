package io.github.openflocon.library.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The sizes of the design system that follow the scale of the application (heights of rows and controls,
 * widths of the columns, of the dropdowns and of the side panels). Never multiply a size by a scale: each
 * [AppScale] preset provides its own values. The defaults are the ones of [AppScale.Default].
 */
@Immutable
data class FloconSizes(
    val controlHeight: Dp = 32.dp,
    val rowHeight: Dp = 32.dp,
    val headerHeight: Dp = 32.dp,
    val menuItemHeight: Dp = 36.dp,
    val pillMinWidth: Dp = 38.dp,
    val timeColumn: Dp = 96.dp,
    val markerColumn: Dp = 22.dp,
    val categoryColumn: Dp = 104.dp,
    val sidePanelWidth: Dp = 280.dp,
    val deviceDropdownWidth: Dp = 220.dp,
    val appDropdownWidth: Dp = 300.dp,
    val topBarButton: Dp = 36.dp,
    val columnToggle: Dp = 56.dp,
    val columnActions: Dp = 40.dp,
    val columnSmall: Dp = 72.dp,
    val columnMedium: Dp = 84.dp,
    val columnLarge: Dp = 130.dp,
    val columnExtraLarge: Dp = 190.dp,
)

val LocalFloconSizes = staticCompositionLocalOf { FloconSizes() }
