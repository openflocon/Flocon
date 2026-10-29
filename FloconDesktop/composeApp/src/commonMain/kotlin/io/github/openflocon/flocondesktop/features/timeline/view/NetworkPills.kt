package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Icon
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
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_network_request
import flocondesktop.composeapp.generated.resources.timeline_network_response
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

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
        "PATCH" -> colors.catTable
        "GRPC" -> colors.catDeeplink
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

// the direction of a network call line: the request goes to the server, the response comes back
@Composable
internal fun PhaseArrow(
    phase: TimelinePhase,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val (image, color, description) = when (phase) {
        TimelinePhase.Request -> Triple(Icons.AutoMirrored.Outlined.ArrowForward, colors.catNetwork, Res.string.timeline_network_request)
        TimelinePhase.Response -> Triple(Icons.AutoMirrored.Outlined.ArrowBack, colors.statusSuccess, Res.string.timeline_network_response)
    }
    Icon(
        imageVector = image,
        contentDescription = stringResource(description),
        tint = color,
        modifier = modifier.size(16.dp),
    )
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
