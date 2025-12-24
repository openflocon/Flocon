package io.github.openflocon.flocondesktop.features.dashboard.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.dashboards
import flocondesktop.composeapp.generated.resources.filter
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardsStateUiModel
import io.github.openflocon.flocondesktop.features.dashboard.model.DeviceDashboardUiModel
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import org.jetbrains.compose.resources.stringResource

@Composable
fun DashboardSidebarView(
    modifier: Modifier = Modifier,
    state: DashboardsStateUiModel,
    onDashboardSelected: (DeviceDashboardUiModel) -> Unit,
    onDeleteClicked: (DeviceDashboardUiModel) -> Unit,
) {
    val borderColor = FloconTheme.colorPalette.secondary

    Surface(
        color = FloconTheme.colorPalette.primary,
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = FloconTheme.shapes.medium
            )
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            Column(
                Modifier.fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(all = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    stringResource(Res.string.dashboards),
                    color = FloconTheme.colorPalette.onSurface,
                    style = FloconTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
                when (state) {
                    DashboardsStateUiModel.Empty -> Unit
                    DashboardsStateUiModel.Loading -> Unit
                    is DashboardsStateUiModel.WithContent -> {
                        var filterText = remember { mutableStateOf("") }
                        val filteredDashboards = remember(filterText.value, state.dashboards) {
                            val filterTextValue = filterText.value
                            if (filterTextValue.isBlank()) {
                                state.dashboards
                            } else {
                                state.dashboards.filter {
                                    it.id.contains(filterTextValue, ignoreCase = true)
                                }
                            }
                        }
                        FilterBar(
                            filterText = filterText,
                            placeholderText = stringResource(Res.string.filter),
                            onTextChange = {
                                filterText.value = it
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(bottom = 12.dp),
                        )
                        filteredDashboards.forEach {
                            DashboardItemView(
                                dashboard = it,
                                onSelect = {
                                    onDashboardSelected(it)
                                },
                                onDelete = {
                                    onDeleteClicked(it)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                isSelected = it.id == state.selected.id
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardItemView(
    dashboard: DeviceDashboardUiModel,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (background, textColor) = if (isSelected) {
        FloconTheme.colorPalette.accent.copy(alpha = 0.4f) to FloconTheme.colorPalette.onAccent
    } else {
        Color.Transparent to FloconTheme.colorPalette.onSurface
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .clickable(onClick = onSelect)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.width(14.dp),
            imageVector = Icons.Outlined.Dashboard,
            contentDescription = null,
            colorFilter = ColorFilter.tint(textColor),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            dashboard.id,
            color = textColor,
            style = FloconTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Box(
            Modifier.clip(RoundedCornerShape(4.dp))
                .background(
                    Color.White.copy(alpha = 0.1f)
                ).padding(2.dp).clickable {
                    onDelete()
                },
            contentAlignment = Alignment.Center,
        ) {
            FloconIcon(
                imageVector = Icons.Outlined.Close,
                tint = textColor.copy(alpha = 0.6f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
