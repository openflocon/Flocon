package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagRowUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagsAction
import io.github.openflocon.flocondesktop.features.featureflags.model.previewFeatureFlagRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme

private val NameWidth = 260.dp
private val VariantWidth = 140.dp

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
    ) {
        FeatureFlagStateDot(
            state = model.state,
            modifier = Modifier.padding(end = 12.dp),
        )
        Text(
            text = model.name,
            modifier = Modifier
                .width(NameWidth)
                .padding(end = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = FloconTheme.colors.ink,
        )
        Box(
            modifier = Modifier
                .width(VariantWidth)
                .padding(end = 12.dp),
        ) {
            FeatureFlagVariantChip(
                state = model.state,
                variant = model.variant,
            )
        }
        model.payloadPreview?.let {
            Text(
                text = it,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = FloconTheme.colors.ink.copy(alpha = 0.5f),
            )
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
