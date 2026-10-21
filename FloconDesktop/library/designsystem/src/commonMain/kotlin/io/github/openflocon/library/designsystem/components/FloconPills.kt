package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.library.designsystem.FloconTheme

// the mono style of the technical data: times, http codes, methods, paths
@Composable
fun floconMonoStyle(small: Boolean = false): TextStyle =
    (if (small) FloconTheme.typography.labelSmall else FloconTheme.typography.labelMedium).copy(fontFamily = FontFamily.Monospace)

// a small colored dot: the color is never alone, a label always goes with it
@Composable
fun FloconDot(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(color)
    )
}

/**
 * An outlined pill for something that has a name and a color, ex: the method of a request.
 * [color] null: neutral.
 */
@Composable
fun FloconOutlinedPill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    leading: (@Composable () -> Unit)? = null,
) {
    val colors = FloconTheme.colors
    Row(
        modifier = modifier
            .defaultMinSize(minWidth = FloconTheme.sizes.pillMinWidth)
            .border(1.dp, color?.copy(alpha = 0.4f) ?: colors.lineStrong, FloconTheme.shapes.small)
            .padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall, Alignment.CenterHorizontally),
    ) {
        leading?.invoke()
        Text(
            text = text,
            style = floconMonoStyle(small = true),
            fontWeight = FontWeight.SemiBold,
            color = color ?: colors.inkMuted,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

/**
 * A solid pill (no border) for something that has a name and a color, ex: the method of a request.
 * [background] and [contentColor] are chosen together to keep the text readable.
 */
@Composable
fun FloconSolidPill(
    text: String,
    background: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .defaultMinSize(minWidth = FloconTheme.sizes.pillMinWidth)
            .clip(FloconTheme.shapes.small)
            .background(background)
            .padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall, Alignment.CenterHorizontally),
    ) {
        leading?.invoke()
        Text(
            text = text,
            style = floconMonoStyle(small = true),
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

/**
 * A status (ex: an http code) with its color: a dot and the text on a soft background of the same color.
 */
@Composable
fun FloconStatusPill(
    color: Color,
    modifier: Modifier = Modifier,
    leading: @Composable () -> Unit = { FloconDot(color = color) },
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(FloconTheme.shapes.small)
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
    ) {
        leading()
        content()
    }
}

@Composable
fun FloconStatusPill(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    FloconStatusPill(color = color, modifier = modifier) {
        Text(
            text = text,
            style = floconMonoStyle(small = true),
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1,
        )
    }
}
