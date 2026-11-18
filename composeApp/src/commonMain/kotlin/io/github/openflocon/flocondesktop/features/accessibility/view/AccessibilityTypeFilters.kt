package io.github.openflocon.flocondesktop.features.accessibility.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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
import flocondesktop.composeapp.generated.resources.accessibility_all_types
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

/**
 * One toggle per event type received, plus "All": keeps only the events of the selected type
 */
@Composable
internal fun AccessibilityTypeFilters(
    types: ImmutableList<String>,
    selectedType: String?,
    onTypeSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TypeFilter(
            label = stringResource(Res.string.accessibility_all_types),
            selected = selectedType == null,
            onClick = { onTypeSelected(null) },
        )
        types.forEach { type ->
            TypeFilter(
                label = accessibilityTypeLabel(type),
                selected = type == selectedType,
                onClick = { onTypeSelected(type) },
            )
        }
    }
}

@Composable
private fun TypeFilter(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = FloconTheme.shapes.medium
    Text(
        text = label,
        style = FloconTheme.typography.bodySmall,
        color = FloconTheme.colors.ink,
        modifier = Modifier
            .clip(shape)
            .background(if (selected) FloconTheme.colors.bg300 else Color.Transparent)
            .border(width = 1.dp, color = FloconTheme.colors.bg300, shape = shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.compact)
            .alpha(if (selected) 1f else 0.5f),
    )
}

@Preview
@Composable
private fun AccessibilityTypeFiltersPreview() {
    FloconTheme {
        AccessibilityTypeFilters(
            types = persistentListOf("ANNOUNCEMENT", "VIEW_FOCUSED"),
            selectedType = "VIEW_FOCUSED",
            onTypeSelected = {},
            modifier = Modifier.padding(FloconTheme.spacing.small),
        )
    }
}
