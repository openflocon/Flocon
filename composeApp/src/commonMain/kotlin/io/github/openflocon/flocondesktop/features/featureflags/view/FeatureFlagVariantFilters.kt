package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagStateUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagVariantFilterUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * One toggle per variant present in the list, colored like the variant chips of the rows
 */
@Composable
internal fun FeatureFlagVariantFilters(
    variants: ImmutableList<FeatureFlagVariantFilterUiModel>,
    hiddenVariants: Set<String>,
    onVariantToggled: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        variants.forEach { variant ->
            FeatureFlagVariantFilter(
                model = variant,
                selected = variant.variant !in hiddenVariants,
                onClick = { onVariantToggled(variant.variant) },
            )
        }
    }
}

@Composable
private fun FeatureFlagVariantFilter(
    model: FeatureFlagVariantFilterUiModel,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = FloconTheme.shapes.medium
    val colors = model.state.colors(model.variant)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) colors.container else Color.Transparent)
            .border(width = 1.dp, color = if (selected) colors.indicator.copy(alpha = 0.5f) else FloconTheme.colors.bg200, shape = shape)
            .toggleable(value = selected, role = Role.Checkbox, onValueChange = { onClick() })
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .alpha(if (selected) 1f else 0.5f),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FeatureFlagStateDot(state = model.state, variant = model.variant)
        Text(
            text = model.variant,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.ink,
            maxLines = 1,
        )
        Text(
            text = model.count.toString(),
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.inkMuted,
        )
    }
}

@Preview
@Composable
private fun FeatureFlagVariantFiltersPreview() {
    FloconTheme {
        FeatureFlagVariantFilters(
            variants = persistentListOf(
                FeatureFlagVariantFilterUiModel("on", FeatureFlagStateUiModel.On, 3),
                FeatureFlagVariantFilterUiModel("off", FeatureFlagStateUiModel.Off, 2),
                FeatureFlagVariantFilterUiModel("control", FeatureFlagStateUiModel.Custom, 1),
                FeatureFlagVariantFilterUiModel("treatment_a", FeatureFlagStateUiModel.Custom, 4),
            ),
            hiddenVariants = setOf("off"),
            onVariantToggled = {},
            modifier = Modifier.padding(FloconTheme.spacing.small),
        )
    }
}
