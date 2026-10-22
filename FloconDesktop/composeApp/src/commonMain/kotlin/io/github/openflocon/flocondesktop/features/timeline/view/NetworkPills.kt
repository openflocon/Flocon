package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

// the mono style of the data: times, http codes, methods, paths
@Composable
internal fun monoStyle(small: Boolean = false) = (if (small) FloconTheme.typography.labelSmall else FloconTheme.typography.labelMedium).copy(fontFamily = FontFamily.Monospace)

@Composable
internal fun MethodPill(
    method: String,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val color = when (method) {
        "GET" -> colors.catNetwork
        "POST" -> colors.catAnalytics
        "WS" -> colors.catDatabase
        "PUT" -> colors.accent
        "DELETE" -> colors.catCrash
        else -> null
    }
    Text(
        text = method,
        style = monoStyle(small = true),
        fontWeight = FontWeight.SemiBold,
        color = color ?: colors.inkMuted,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = modifier
            .defaultMinSize(minWidth = FloconTheme.sizes.pillMinWidth)
            .border(1.dp, color?.copy(alpha = 0.4f) ?: colors.lineStrong, FloconTheme.shapes.small)
            .padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.tiny),
    )
}

// the http code, colored by family
@Composable
internal fun StatusPill(
    code: Int,
    modifier: Modifier = Modifier,
) {
    val color = FloconTheme.colors.httpStatus(code)
    Row(
        modifier = modifier
            .clip(FloconTheme.shapes.small)
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
    ) {
        CategoryDot(color = color)
        Text(
            text = code.toString(),
            style = monoStyle(small = true),
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1,
        )
    }
}

// a small neutral label, ex: the provider of an analytics event
@Composable
internal fun TagPill(
    text: String,
    modifier: Modifier = Modifier,
    background: Color = FloconTheme.colors.bg300,
) {
    Text(
        text = text,
        style = FloconTheme.typography.labelSmall,
        color = FloconTheme.colors.inkMuted,
        maxLines = 1,
        modifier = modifier
            .clip(CircleShape)
            .background(background)
            .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny),
    )
}
