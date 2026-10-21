package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * The variant of a flag, colored by its state: on (accent), off (outlined), custom (neutral)
 */
@Composable
internal fun FeatureFlagVariantChip(
    state: FeatureFlagStateUiModel,
    variant: String,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colorPalette
    val (containerColor, contentColor) = when (state) {
        FeatureFlagStateUiModel.On -> colors.accent to colors.onAccent
        FeatureFlagStateUiModel.Off -> Color.Transparent to colors.onSecondary
        FeatureFlagStateUiModel.Custom -> colors.secondary to colors.onSecondary
    }
    Row(
        modifier = modifier
            .background(color = containerColor, shape = FloconTheme.shapes.extraLarge)
            .then(
                if (state == FeatureFlagStateUiModel.Off) {
                    Modifier.border(width = 1.dp, color = colors.secondary, shape = FloconTheme.shapes.extraLarge)
                } else Modifier
            )
            .padding(horizontal = 8.dp, vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        FeatureFlagStateDot(state = state)
        Text(
            text = variant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = FloconTheme.typography.bodySmall,
            color = contentColor,
        )
    }
}

@Composable
internal fun FeatureFlagStateDot(
    state: FeatureFlagStateUiModel,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colorPalette
    Box(
        modifier = modifier
            .size(6.dp)
            .then(
                when (state) {
                    FeatureFlagStateUiModel.On -> Modifier.background(color = colors.onAccent, shape = CircleShape)
                    FeatureFlagStateUiModel.Off -> Modifier.border(width = 1.dp, color = colors.onSecondary, shape = CircleShape)
                    FeatureFlagStateUiModel.Custom -> Modifier.background(color = colors.onSecondary, shape = CircleShape)
                }
            ),
    )
}

@Preview
@Composable
private fun FeatureFlagVariantChipPreview() {
    FloconTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FeatureFlagVariantChip(state = FeatureFlagStateUiModel.On, variant = "on")
            FeatureFlagVariantChip(state = FeatureFlagStateUiModel.Off, variant = "control")
            FeatureFlagVariantChip(state = FeatureFlagStateUiModel.Custom, variant = "variant_b")
        }
    }
}
