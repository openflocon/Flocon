package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.feature_flags_column_name
import flocondesktop.composeapp.generated.resources.feature_flags_column_payload
import flocondesktop.composeapp.generated.resources.feature_flags_column_variant
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagRowUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagsAction
import io.github.openflocon.flocondesktop.features.featureflags.model.previewFeatureFlagRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconTableHeaderLabel
import io.github.openflocon.library.designsystem.components.floconTableHeader
import org.jetbrains.compose.resources.stringResource

private val NameWidth = 260.dp
private val VariantWidth = 140.dp

// state dot (8.dp) + its end padding (12.dp)
private val StateColumnWidth = 20.dp

@Composable
fun FeatureFlagRow(
    model: FeatureFlagRowUiModel,
    onAction: (FeatureFlagsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = { onAction(FeatureFlagsAction.OnClick(model)) })
            .padding(vertical = FloconTheme.spacing.small, horizontal = FloconTheme.spacing.medium),
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

@Composable
fun FeatureFlagsHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .floconTableHeader()
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(StateColumnWidth))
        FloconTableHeaderLabel(
            text = stringResource(Res.string.feature_flags_column_name),
            modifier = Modifier.width(NameWidth).padding(end = FloconTheme.spacing.medium),
        )
        FloconTableHeaderLabel(
            text = stringResource(Res.string.feature_flags_column_variant),
            modifier = Modifier.width(VariantWidth).padding(end = FloconTheme.spacing.medium),
        )
        FloconTableHeaderLabel(
            text = stringResource(Res.string.feature_flags_column_payload),
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview
@Composable
private fun FeatureFlagRowPreview() {
    FloconTheme {
        FeatureFlagRow(
            model = previewFeatureFlagRowUiModel(),
            onAction = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
