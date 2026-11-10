package io.github.openflocon.flocondesktop.features.analytics.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.analytics_selector_caption
import flocondesktop.composeapp.generated.resources.no_analytics
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.DeviceAnalyticsUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.DropdownFilterField
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconPageSelector
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AnalyticsSelector(
    analyticsState: AnalyticsStateUiModel,
    onAnalyticsSelected: (DeviceAnalyticsUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = {
            if (analyticsState is AnalyticsStateUiModel.WithContent) {
                expanded = true
            }
        },
        anchorContent = {
            val onClick = {
                if (analyticsState is AnalyticsStateUiModel.WithContent) {
                    expanded = true
                }
            }
            when (analyticsState) {
                AnalyticsStateUiModel.Empty -> Unit

                AnalyticsStateUiModel.Loading ->
                    FloconButton(onClick = onClick, containerColor = FloconTheme.colors.bg200) {
                        FloconLinearProgressIndicator()
                    }

                is AnalyticsStateUiModel.WithContent -> FloconPageSelector(
                    icon = Icons.Outlined.TableChart,
                    caption = stringResource(Res.string.analytics_selector_caption),
                    value = analyticsState.selected.name,
                    onClick = onClick,
                )
            }
        },
        modifier = modifier
    ) {
        var filterText by remember { mutableStateOf("") }
        DropdownFilterField(
            value = filterText,
            onValueChanged = {
                filterText = it
            },
            modifier = Modifier.padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny)
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
