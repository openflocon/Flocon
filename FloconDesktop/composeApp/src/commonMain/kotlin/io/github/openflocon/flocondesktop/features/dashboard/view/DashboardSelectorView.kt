package io.github.openflocon.flocondesktop.features.dashboard.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardsStateUiModel
import io.github.openflocon.flocondesktop.features.dashboard.model.DeviceDashboardUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.DropdownFilterFieldView
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder

@Composable
internal fun DashboardSelectorView(
    dashboardsState: DashboardsStateUiModel,
    onDashboardSelected: (DeviceDashboardUiModel) -> Unit,
    onDeleteClicked: (DeviceDashboardUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = {
            if (dashboardsState is DashboardsStateUiModel.WithContent) expanded = true
        },
        anchorContent = {
            FloconButton(
                onClick = {
                    if (dashboardsState is DashboardsStateUiModel.WithContent) expanded = true
                }, containerColor = FloconTheme.colorPalette.secondary
            ) {
                when (dashboardsState) {
                    DashboardsStateUiModel.Empty -> Text(
                        "No dashboard",
                        style = FloconTheme.typography.bodySmall
                    )

                    DashboardsStateUiModel.Loading -> FloconLinearProgressIndicator()
                    is DashboardsStateUiModel.WithContent -> {
                        Text(
                            text = dashboardsState.selected.id,
                            style = FloconTheme.typography.bodySmall
                        )
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
            if (dashboardsState is DashboardsStateUiModel.WithContent) {
                val filteredItems = remember(filterText, dashboardsState.dashboards) {
                    dashboardsState.dashboards.filter {
                        it.id.contains(
                            filterText,
                            ignoreCase = true
                        )
                    }
                }

                filteredItems.fastForEach { dashboard ->
                    FloconDropdownMenuItem(
                        text = dashboard.id,
                        onClick = {
                            onDashboardSelected(dashboard)
                            expanded = false
                        },
                        secondaryAction = {
                            Box(
                                Modifier.clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Color.White.copy(alpha = 0.8f)
                                    ).padding(2.dp).clickable {
                                        onDeleteClicked(dashboard)
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                FloconIcon(
                                    imageVector = Icons.Outlined.Close,
                                    tint = FloconTheme.colorPalette.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
