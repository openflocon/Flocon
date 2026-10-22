package io.github.openflocon.flocondesktop.features.network.mock.list.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.mock.edition.view.MockNetworkMethodView
import io.github.openflocon.flocondesktop.features.network.mock.list.model.MockNetworkLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconSwitch

@Composable
fun MockLineView(
    item: MockNetworkLineUiModel,
    onClicked: (id: String) -> Unit,
    onDeleteClicked: (id: String) -> Unit,
    changeIsEnabled: (id: String, enabled: Boolean) -> Unit,
    changeIsShared: (id: String, shared: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    // a disabled mock is dimmed, but it stays readable
    val dimmed = if (item.isEnabled) 1f else 0.6f

    Row(
        modifier = modifier
            .background(if (hovered) colors.bg300 else Color.Transparent)
            .hoverable(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = { onClicked(item.id) })
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
                onCheckedChange = { changeIsEnabled(item.id, it) }
            )
        }
        Box(Modifier.width(FloconTheme.sizes.columnSmall).alpha(dimmed)) {
            MockNetworkMethodView(method = item.method)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.displayName.ifBlank { "No name" },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = FloconTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = colors.ink,
                modifier = Modifier.alpha(dimmed),
            )
            Text(
                text = item.urlPattern,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = FloconTheme.typography.monoMedium,
                color = colors.inkMuted,
                modifier = Modifier.alpha(dimmed),
            )
        }
        Box(Modifier.width(FloconTheme.sizes.columnSmall)) {
            FloconCheckbox(
                checked = item.isShared,
                onCheckedChange = {
                    changeIsShared(item.id, !item.isShared)
                }
            )
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

@Preview
@Composable
private fun MockLineViewPreview() {
    FloconTheme {
        FloconSurface {
            MockLineView(
                item = MockNetworkLineUiModel(
                    id = "",
                    urlPattern = ".*",
                    isEnabled = true,
                    method = MockNetworkMethodUi.GET,
                    isShared = false,
                    displayName = "Mock for YouTube video",
                ),
                onClicked = {},
                onDeleteClicked = {},
                changeIsEnabled = { _, _ -> },
                changeIsShared = { _, _ -> },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
