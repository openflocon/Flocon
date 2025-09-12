package io.github.openflocon.flocondesktop.features.analytics.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.DeviceAnalyticsUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.DropdownFilterFieldView
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
                    AnalyticsStateUiModel.Empty -> Text("No analytics", style = FloconTheme.typography.bodySmall)
                    AnalyticsStateUiModel.Loading -> FloconLinearProgressIndicator()
                    is AnalyticsStateUiModel.WithContent -> {
                        Text(text = analyticsState.selected.name, style = FloconTheme.typography.bodySmall)
                        FloconIcon(
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) {
        var filterText by remember { mutableStateOf("") }
        DropdownFilterFieldView(
            value = filterText,
            onValueChanged = {
                filterText = it
            },
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            if (analyticsState is AnalyticsStateUiModel.WithContent) {
                val filteredItems = remember(filterText, analyticsState.analytics) {
                    analyticsState.analytics.filter {
                        it.name.contains(
                            filterText,
                            ignoreCase = true
                        )
                    }
                }

                filteredItems.fastForEach { analytics ->
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
}
