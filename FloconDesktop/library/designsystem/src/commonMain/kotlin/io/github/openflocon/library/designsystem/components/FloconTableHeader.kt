package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.github.openflocon.library.designsystem.FloconTheme

/** Small capitals used by the column names of every table of the app. */
@Composable
@ReadOnlyComposable
fun floconTableHeaderTextStyle(): TextStyle = FloconTheme.typography.labelSmall.copy(
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.08.em,
)

/** Color of the column names (and of their sort arrows) of every table of the app. */
val floconTableHeaderTextColor: Color
    @Composable @ReadOnlyComposable
    get() = FloconTheme.colors.ink.copy(alpha = 0.8f)

@Composable
fun Modifier.floconTableHeaderBackground() = background(FloconTheme.colors.bg000.copy(alpha = 0.1f))

/** Background (and height) of the row holding the column names of a table. */
@Composable
fun Modifier.floconTableHeader(height: Dp = FloconTheme.sizes.headerHeight): Modifier = this
    .height(height)
    .floconTableHeaderBackground()
    .headerBottomLine()

@Composable
private fun Modifier.headerBottomLine(): Modifier {
    val color = FloconTheme.colors.line
    return drawBehind {
        val y = size.height - 0.5.dp.toPx()
        drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
    }
}

/** A column name: uppercase (unless [uppercase] is false, for data names where case matters), faint. */
@Composable
fun FloconTableHeaderLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = floconTableHeaderTextColor,
    style: TextStyle = floconTableHeaderTextStyle(),
    uppercase: Boolean = true,
    textAlign: TextAlign? = null,
) {
    Text(
        text = if (uppercase) text.uppercase() else text,
        style = style,
        color = color,
        textAlign = textAlign,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}
