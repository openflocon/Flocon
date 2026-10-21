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
import androidx.compose.ui.text.font.FontWeight
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
import io.github.openflocon.library.designsystem.components.floconMonoStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import androidx.compose.desktop.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_copy_value
import flocondesktop.composeapp.generated.resources.network_decode_jwt

@Composable
fun DetailHeadersView(
    headers: ImmutableList<NetworkDetailHeaderUi>,
    labelWidth: Dp,
    onAuthorizationClicked: (value: String) -> Unit,
    modifier: Modifier = Modifier,
    onCopyValue: ((value: String) -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .background(FloconTheme.colors.bg200, FloconTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = FloconTheme.colors.line,
                shape = FloconTheme.shapes.medium
            )
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.extraSmall)
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
                    color = FloconTheme.colors.line
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
        modifier = modifier
            .padding(vertical = FloconTheme.spacing.extraSmall)
            .onPointerEvent(PointerEventType.Enter) { isTitleHovered = true }
            .onPointerEvent(PointerEventType.Exit) { isTitleHovered = false },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall)
    ) {
        Box(
            modifier = Modifier
                .width(labelWidth)
                .padding(end = FloconTheme.spacing.small),
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
                        style = floconMonoStyle(),
                        fontWeight = FontWeight.Medium,
                        color = FloconTheme.colors.ink,
                    )
                }
                if (isTitleHovered && onCopyValue != null) {
                    FloconSmallIconButton(
                        imageVector = Icons.Outlined.ContentCopy,
                        onClick = { onCopyValue(item.value) },
                        contentDescription = stringResource(Res.string.general_copy_value),
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
            Spacer(modifier = Modifier.width(FloconTheme.spacing.extraSmall))
            FloconButton(
                onClick = { onAuthorizationClicked(item.value) }
            ) {
                Text(stringResource(Res.string.network_decode_jwt), textAlign = TextAlign.Center, color = FloconTheme.colors.ink)
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
                        .background(FloconTheme.colors.bg200)
                        .border(
                            width = 1.dp,
                            color = FloconTheme.colors.accent.copy(alpha = 0.5f),
                            shape = FloconTheme.shapes.small
                        )
                        .padding(FloconTheme.spacing.small)
                ) {
                    Text(
                        text = value,
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colors.inkMuted,
                    )
                }
            },
            delayMillis = 200,
            tooltipPlacement = TooltipPlacement.ComponentRect(
                offset = DpOffset(x = FloconTheme.spacing.none, y = FloconTheme.spacing.extraSmall)
            ),
            modifier = modifier
        ) {
            SelectionContainer {
                Text(
                    text = value,
                    style = floconMonoStyle(),
                    color = FloconTheme.colors.inkMuted,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    } else {
        SelectionContainer(modifier = modifier) {
            Text(
                text = value,
                style = floconMonoStyle(),
                color = FloconTheme.colors.inkMuted,
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
            headers = persistentListOf(
                previewNetworkDetailHeaderUi()
            ),
            labelWidth = 100.dp,
            modifier = Modifier.fillMaxWidth(),
            onAuthorizationClicked = {},
            onCopyValue = {},
        )
    }
}

