package io.github.openflocon.flocondesktop.features.dashboard.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardsStateUiModel
import io.github.openflocon.flocondesktop.features.dashboard.model.DeviceDashboardUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator

@Composable
internal fun DashboardSelectorView(
    dashboardsState: DashboardsStateUiModel,
    onDashboardSelected: (DeviceDashboardUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = {
            if (dashboardsState is DashboardsStateUiModel.WithContent)
                expanded = true
        },
        anchorContent = {
            FloconButton(
                onClick = {
                    if (dashboardsState is DashboardsStateUiModel.WithContent)
                        expanded = true
                },
                containerColor = FloconTheme.colorPalette.secondary
            ) {
                when (dashboardsState) {
                    DashboardsStateUiModel.Empty -> Text("No database")
                    DashboardsStateUiModel.Loading -> FloconLinearProgressIndicator()
                    is DashboardsStateUiModel.WithContent -> {
                        Text(text = dashboardsState.selected.id)
                        FloconIcon(
                            imageVector = Icons.Outlined.KeyboardArrowDown
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) {
        if (dashboardsState is DashboardsStateUiModel.WithContent) {
            dashboardsState.dashboards
                .forEach { dashboard ->
                    FloconDropdownMenuItem(
                        text = dashboard.id,
                        onClick = {
                            onDashboardSelected(dashboard)
                            expanded = false
                        }
                    )
                }
        }
    }
}
