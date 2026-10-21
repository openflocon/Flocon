package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.feature_flag_state_custom
import flocondesktop.composeapp.generated.resources.feature_flag_state_off
import flocondesktop.composeapp.generated.resources.feature_flag_state_on
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

/**
 * One toggle per state: keeps only the flags of the selected states
 */
@Composable
internal fun FeatureFlagStateFilters(
    selectedStates: Set<FeatureFlagStateUiModel>,
    onStateToggled: (FeatureFlagStateUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FeatureFlagStateUiModel.entries.forEach { state ->
            FeatureFlagStateFilter(
                state = state,
                selected = state in selectedStates,
                onClick = { onStateToggled(state) },
            )
        }
    }
}

@Composable
private fun FeatureFlagStateFilter(
    state: FeatureFlagStateUiModel,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = FloconTheme.shapes.medium
    Row(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) FloconTheme.colors.bg200 else Color.Transparent)
            .border(width = 1.dp, color = FloconTheme.colors.bg200, shape = shape)
            .toggleable(value = selected, role = Role.Checkbox, onValueChange = { onClick() })
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .alpha(if (selected) 1f else 0.5f),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FeatureFlagStateDot(state = state)
        Text(
            text = stringResource(
                when (state) {
                    FeatureFlagStateUiModel.On -> Res.string.feature_flag_state_on
                    FeatureFlagStateUiModel.Off -> Res.string.feature_flag_state_off
                    FeatureFlagStateUiModel.Custom -> Res.string.feature_flag_state_custom
                }
            ),
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.ink,
        )
    }
}

@Preview
@Composable
private fun FeatureFlagStateFiltersPreview() {
    FloconTheme {
        FeatureFlagStateFilters(
            selectedStates = setOf(FeatureFlagStateUiModel.On, FeatureFlagStateUiModel.Custom),
            onStateToggled = {},
            modifier = Modifier.padding(FloconTheme.spacing.small),
        )
    }
}
