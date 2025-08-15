package io.github.openflocon.flocondesktop.features.network.view.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DetailSectionTitleView(
    isExpanded: Boolean,
    title: String,
    modifier: Modifier = Modifier,
    onDetail: (() -> Unit)? = null,
    onCopy: (() -> Unit)? = null,
    onToggle: ((isExpanded: Boolean) -> Unit)? = null,
) {
    val rotate by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotate",
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onToggle != null) {
            // Toggle Button for Request Body
            FloconIconButton(
                onClick = { onToggle(!isExpanded) },
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.ExpandMore,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = rotate
                    },
                )
            }
        }
        Text(
            text = title,
            style = FloconTheme.typography.titleMedium,
            color = FloconTheme.colorPalette.onBackground,
            modifier = Modifier.weight(1f), // Takes remaining space
        )

        if (onDetail != null) {
            FloconIconButton(
                onClick = onDetail,
                imageVector = Icons.Outlined.OpenInFull,
            )
        }
        if (onCopy != null) {
            FloconIconButton(
                imageVector = Icons.Outlined.CopyAll,
                onClick = onCopy,
            )
        }
    }
}

@Preview
@Composable
private fun CodeBlockView_expanded_Preview() {
    FloconTheme {
        DetailSectionTitleView(
            isExpanded = true,
            title = "title",
            onCopy = {},
            onToggle = {},
        )
    }
}

@Preview
@Composable
private fun CodeBlockView_closed_Preview() {
    FloconTheme {
        DetailSectionTitleView(
            isExpanded = false,
            title = "title",
            onCopy = {},
            onToggle = {},
        )
    }
}
