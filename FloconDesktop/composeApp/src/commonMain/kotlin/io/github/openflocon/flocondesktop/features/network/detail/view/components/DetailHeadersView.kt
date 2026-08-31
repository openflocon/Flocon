@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop.features.network.detail.view.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.border

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.detail.model.previewNetworkDetailHeaderUi
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconSmallIconButton
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DetailHeadersView(
    headers: List<NetworkDetailHeaderUi>,
    labelWidth: Dp,
    onAuthorizationClicked: (value: String) -> Unit,
    modifier: Modifier = Modifier,
    onCopyValue: ((value: String) -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = FloconTheme.colorPalette.secondary,
                shape = FloconTheme.shapes.medium
            )
            .padding(8.dp)
    ) {
        headers.fastForEachIndexed { index, item ->
            HeaderItemRow(
                item = item,
                labelWidth = labelWidth,
                onAuthorizationClicked = onAuthorizationClicked,
                onCopyValue = onCopyValue,
                modifier = Modifier.fillMaxWidth()
            )
            if (index != headers.lastIndex) {
                FloconHorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = FloconTheme.colorPalette.secondary
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun HeaderItemRow(
    item: NetworkDetailHeaderUi,
    labelWidth: Dp,
    onAuthorizationClicked: (value: String) -> Unit,
    onCopyValue: ((value: String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    var isTitleHovered by remember { mutableStateOf(false) }
    val isAuthBearer = remember(item.name, item.value) {
        item.name.equals("authorization", ignoreCase = true) && item.value.startsWith("Bearer ")
    }

    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Header Name / Title with hover-copy icon
        Box(
            modifier = Modifier
                .width(labelWidth)
                .padding(end = 8.dp)
                .onPointerEvent(PointerEventType.Enter) { isTitleHovered = true }
                .onPointerEvent(PointerEventType.Exit) { isTitleHovered = false },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SelectionContainer {
                    Text(
                        text = item.name,
                        style = FloconTheme.typography.titleSmall,
                        color = FloconTheme.colorPalette.onPrimary,
                    )
                }
                if (isTitleHovered && onCopyValue != null) {
                    FloconSmallIconButton(
                        imageVector = Icons.Outlined.ContentCopy,
                        onClick = { onCopyValue(item.value) },
                        contentDescription = "Copy value",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Header Value with max 10 lines and hover tooltip
        HeaderValueText(
            value = item.value,
            modifier = Modifier.weight(1f)
        )

        if (isAuthBearer) {
            Spacer(modifier = Modifier.width(4.dp))
            FloconButton(
                onClick = { onAuthorizationClicked(item.value) }
            ) {
                Text("Decode\nJWT", textAlign = TextAlign.Center, color = FloconTheme.colorPalette.onPrimary)
            }
        }
    }
}

@Composable
private fun HeaderValueText(
    value: String,
    modifier: Modifier = Modifier
) {
    val isLarge = remember(value) { value.lines().size > 10 || value.length > 200 }

    if (isLarge) {
        TooltipArea(
            tooltip = {
                Box(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .clip(FloconTheme.shapes.small)
                        .background(FloconTheme.colorPalette.secondary)
                        .border(
                            width = 1.dp,
                            color = FloconTheme.colorPalette.accent.copy(alpha = 0.5f),
                            shape = FloconTheme.shapes.small
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = value,
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colorPalette.onSecondary,
                    )
                }
            },
            delayMillis = 200,
            tooltipPlacement = TooltipPlacement.ComponentRect(
                offset = DpOffset(x = 0.dp, y = 4.dp)
            ),
            modifier = modifier
        ) {
            SelectionContainer {
                Text(
                    text = value,
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colorPalette.onPrimary,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    } else {
        SelectionContainer(modifier = modifier) {
            Text(
                text = value,
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colorPalette.onPrimary,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}


@Preview
@Composable
private fun DetailHeadersViewPreview() {
    FloconTheme {
        DetailHeadersView(
            headers = listOf(
                previewNetworkDetailHeaderUi()
            ),
            labelWidth = 100.dp,
            modifier = Modifier.fillMaxWidth(),
            onAuthorizationClicked = {},
            onCopyValue = {},
        )
    }
}

