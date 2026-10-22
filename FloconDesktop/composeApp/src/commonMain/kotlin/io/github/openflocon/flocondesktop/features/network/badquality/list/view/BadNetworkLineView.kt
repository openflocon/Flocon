package io.github.openflocon.flocondesktop.features.network.badquality.list.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.badquality.list.model.NetworkBadQualityLineUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.list.model.previewNetworkBadQualityLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconStatusPill
import io.github.openflocon.library.designsystem.components.FloconSwitch

// the columns of the list of profiles, shared by its header and its lines
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun BadNetworkLineView(
    item: NetworkBadQualityLineUiModel,
    onClicked: (id: String) -> Unit,
    onDeleteClicked: (id: String) -> Unit,
    enableClicked: (id: String, enabled: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    // a disabled profile is dimmed, but it stays readable
    val dimmed = if (item.isEnabled) 1f else 0.6f

    Row(
        modifier = modifier
            .background(if (hovered) colors.bg300 else Color.Transparent)
            .hoverable(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null) { onClicked(item.id) }
            .drawBehind {
                drawLine(
                    color = colors.line,
                    start = Offset(0f, size.height - 0.5.dp.toPx()),
                    end = Offset(size.width, size.height - 0.5.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        Box(Modifier.width(FloconTheme.sizes.columnToggle)) {
            FloconSwitch(
                checked = item.isEnabled,
                onCheckedChange = { enableClicked(item.id, it) }
            )
        }
        Text(
            text = item.name,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = FloconTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = colors.ink,
            modifier = Modifier
                .weight(1f)
                .alpha(dimmed),
        )
        MonoCell(item.trigger, FloconTheme.sizes.columnSmall, dimmed)
        MonoCell(item.latency, FloconTheme.sizes.columnLarge, dimmed)
        MonoCell(item.errorRate, FloconTheme.sizes.columnMedium, dimmed)
        FlowRow(
            modifier = Modifier.width(FloconTheme.sizes.columnExtraLarge),
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
        ) {
            if (item.errors.isEmpty()) {
                Text(
                    text = "-",
                    style = FloconTheme.typography.labelMedium,
                    color = colors.inkFaint,
                )
            }
            item.errors.forEach { error ->
                FloconStatusPill(
                    text = error.label,
                    // an exception is an error, a http code is a warning
                    color = if (error.isException) colors.statusError else colors.statusWarning,
                )
            }
        }
        Box(Modifier.width(FloconTheme.sizes.columnActions)) {
            FloconIconButton(
                imageVector = Icons.Filled.Delete,
                onClick = {
                    onDeleteClicked(item.id)
                },
            )
        }
    }
}

@Composable
private fun MonoCell(
    text: String,
    width: Dp,
    alpha: Float,
) {
    Text(
        text = text,
        style = FloconTheme.typography.monoMedium,
        color = FloconTheme.colors.inkMuted,
        maxLines = 1,
        modifier = Modifier
            .width(width)
            .alpha(alpha),
    )
}

@Preview
@Composable
private fun BadNetworkLineViewPreview() {
    FloconTheme {
        BadNetworkLineView(
            item = previewNetworkBadQualityLineUiModel(),
            onClicked = {},
            onDeleteClicked = {},
            enableClicked = { _, _ -> },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
