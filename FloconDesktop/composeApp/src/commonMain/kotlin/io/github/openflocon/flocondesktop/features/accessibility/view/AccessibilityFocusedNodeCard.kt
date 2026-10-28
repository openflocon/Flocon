package io.github.openflocon.flocondesktop.features.accessibility.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.accessibility_focus_next
import flocondesktop.composeapp.generated.resources.accessibility_focus_none
import flocondesktop.composeapp.generated.resources.accessibility_focus_position
import flocondesktop.composeapp.generated.resources.accessibility_focus_previous
import flocondesktop.composeapp.generated.resources.accessibility_focus_refresh
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityAction
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityFocusedNodeUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import org.jetbrains.compose.resources.stringResource

/**
 * The element the screen reader is on, with the buttons to move to the previous / next one
 */
@Composable
internal fun AccessibilityFocusedNodeCard(
    focusedNode: AccessibilityFocusedNodeUiModel?,
    canMoveFocus: Boolean,
    onAction: (AccessibilityAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colors.bg100)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
    ) {
        FloconIconButton(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            tooltip = stringResource(Res.string.accessibility_focus_previous),
            enabled = canMoveFocus,
            onClick = { onAction(AccessibilityAction.OnFocusPreviousClicked) },
        )
        FloconIconButton(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            tooltip = stringResource(Res.string.accessibility_focus_next),
            enabled = canMoveFocus,
            onClick = { onAction(AccessibilityAction.OnFocusNextClicked) },
        )
        FloconIconButton(
            imageVector = Icons.Outlined.Refresh,
            tooltip = stringResource(Res.string.accessibility_focus_refresh),
            enabled = canMoveFocus,
            onClick = { onAction(AccessibilityAction.OnRefreshFocusedNodeClicked) },
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
            modifier = Modifier.weight(1f),
        ) {
            if (focusedNode == null) {
                Text(
                    text = stringResource(Res.string.accessibility_focus_none),
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colors.ink.copy(alpha = 0.6f),
                )
            } else {
                Text(
                    text = focusedNode.title,
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colors.ink,
                )
                val details = listOfNotNull(
                    focusedNode.position?.let {
                        stringResource(Res.string.accessibility_focus_position, it.number, it.count)
                    },
                    focusedNode.className,
                    focusedNode.bounds,
                ).joinToString(" · ")
                if (details.isNotEmpty()) {
                    Text(
                        text = details,
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colors.ink.copy(alpha = 0.6f),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AccessibilityFocusedNodeCardPreview() {
    FloconTheme {
        AccessibilityFocusedNodeCard(
            focusedNode = AccessibilityFocusedNodeUiModel(
                title = "Add to cart",
                className = "Button",
                bounds = "[48,1530][1032,1674]",
                screen = "CartActivity",
                position = AccessibilityFocusedNodeUiModel.Position(number = 3, count = 12),
            ),
            canMoveFocus = true,
            onAction = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun AccessibilityFocusedNodeCardNoneFocusedPreview() {
    FloconTheme {
        AccessibilityFocusedNodeCard(
            focusedNode = null,
            canMoveFocus = false,
            onAction = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
