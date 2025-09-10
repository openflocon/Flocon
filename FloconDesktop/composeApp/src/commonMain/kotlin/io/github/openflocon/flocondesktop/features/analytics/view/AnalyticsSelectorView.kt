package io.github.openflocon.flocondesktop.features.analytics.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.DeviceAnalyticsUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator

@Composable
internal fun AnalyticsSelectorView(
    analyticsState: AnalyticsStateUiModel,
    onAnalyticsSelected: (DeviceAnalyticsUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = {
            if (analyticsState is AnalyticsStateUiModel.WithContent)
                expanded = true
        },
        anchorContent = {
            FloconButton(
                onClick = {
                    if (analyticsState is AnalyticsStateUiModel.WithContent)
                        expanded = true
                },
                containerColor = FloconTheme.colorPalette.secondary
            ) {
                when (analyticsState) {
                    AnalyticsStateUiModel.Empty -> Text("No analytics")
                    AnalyticsStateUiModel.Loading -> FloconLinearProgressIndicator()
                    is AnalyticsStateUiModel.WithContent -> {
                        Text(text = analyticsState.selected.name)
                        FloconIcon(
                            imageVector = Icons.Outlined.KeyboardArrowDown
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) {
        if (analyticsState is AnalyticsStateUiModel.WithContent) {
            analyticsState.analytics
                .forEach { analytics ->
                    FloconDropdownMenuItem(
                        text = analytics.name,
                        onClick = {
                            onAnalyticsSelected(analytics)
                            expanded = false
                        }
                    )
                }
        }
    }
}
