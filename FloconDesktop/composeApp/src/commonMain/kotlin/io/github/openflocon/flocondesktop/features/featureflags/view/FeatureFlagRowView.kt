package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagRowUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagsAction
import io.github.openflocon.flocondesktop.features.featureflags.model.previewFeatureFlagRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FeatureFlagRowView(
    model: FeatureFlagRowUiModel,
    onAction: (FeatureFlagsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = { onAction(FeatureFlagsAction.OnClick(model)) })
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = model.provider,
            modifier = Modifier.width(140.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
            color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.6f),
        )
        Text(
            text = model.name,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colorPalette.onPrimary,
        )
        Box(
            Modifier
                .background(
                    shape = FloconTheme.shapes.extraLarge,
                    color = FloconTheme.colorPalette.secondary
                )
                .padding(horizontal = 8.dp, vertical = 1.dp),
        ) {
            Text(
                text = model.variant,
                maxLines = 1,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onSecondary,
            )
        }
        Box(Modifier.size(16.dp)) {
            if (model.hasPayload) {
                Icon(
                    imageVector = Icons.Outlined.DataObject,
                    contentDescription = null,
                    tint = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun FeatureFlagRowViewPreview() {
    FloconTheme {
        FeatureFlagRowView(
            model = previewFeatureFlagRowUiModel(),
            onAction = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
