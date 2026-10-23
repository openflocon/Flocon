package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material.icons.sharp.Upload
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton

/** Bar displayed at the bottom of the list while selecting several lines. */
@Composable
fun NetworkSelectionBar(
    selectedCount: Int,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = FloconTheme.spacing.small,
                horizontal = FloconTheme.spacing.medium
            )
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colors.bg200)
            .padding(FloconTheme.spacing.small)
    ) {
        Text(
            text = "$selectedCount items selected",
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.inkMuted,
            modifier = Modifier.padding(start = FloconTheme.spacing.small),
        )
        Spacer(Modifier.weight(1f))
        FloconIconTonalButton(
            onClick = { onAction(NetworkAction.ExportCsv) },
            containerColor = FloconTheme.colors.ink
        ) {
            FloconIcon(Icons.Sharp.Upload)
        }
        FloconIconTonalButton(
            onClick = { onAction(NetworkAction.DeleteSelection) },
            containerColor = FloconTheme.colors.ink
        ) {
            FloconIcon(Icons.Sharp.Delete)
        }
        FloconButton(
            onClick = { onAction(NetworkAction.ClearMultiSelect) },
            containerColor = Color.Transparent,
        ) {
            FloconIcon(
                Icons.Sharp.Close,
                tint = FloconTheme.colors.inkMuted
            )
        }
    }
}

@Composable
@Preview
private fun NetworkSelectionBarPreview() {
    FloconTheme {
        NetworkSelectionBar(
            selectedCount = 3,
            onAction = {},
        )
    }
}
