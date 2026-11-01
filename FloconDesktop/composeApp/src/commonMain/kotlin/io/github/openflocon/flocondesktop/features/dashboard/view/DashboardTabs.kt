package io.github.openflocon.flocondesktop.features.dashboard.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.dashboard.model.DeviceDashboardUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val CloseIconSize = 12.dp

@Composable
fun DashboardTabs(
    dashboards: ImmutableList<DeviceDashboardUiModel>,
    selectedId: String,
    onDashboardSelected: (DeviceDashboardUiModel) -> Unit,
    onDeleteClicked: (DeviceDashboardUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(dashboards, key = { it.id }) { dashboard ->
            DashboardTab(
                dashboard = dashboard,
                isSelected = dashboard.id == selectedId,
                onSelect = { onDashboardSelected(dashboard) },
                onDelete = { onDeleteClicked(dashboard) },
            )
        }
    }
}

@Composable
private fun DashboardTab(
    dashboard: DeviceDashboardUiModel,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val textColor = if (isSelected) colors.accent else colors.inkMuted

    Row(
        modifier = modifier
            .height(FloconTheme.sizes.pageBarControlHeight)
            .clip(FloconTheme.shapes.medium)
            .background(if (isSelected) colors.accent.copy(alpha = 0.16f) else Color.Transparent)
            .clickable(onClick = onSelect)
            .padding(start = FloconTheme.spacing.medium, end = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Text(
            text = dashboard.id,
            color = textColor,
            style = FloconTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(
            modifier = Modifier
                .clip(FloconTheme.shapes.small)
                .clickable(onClick = onDelete)
                .padding(FloconTheme.spacing.tiny),
            contentAlignment = Alignment.Center,
        ) {
            FloconIcon(
                imageVector = Icons.Outlined.Close,
                tint = textColor,
                modifier = Modifier.size(CloseIconSize),
            )
        }
    }
}

@Preview
@Composable
private fun DashboardTabsPreview() {
    FloconTheme {
        DashboardTabs(
            dashboards = persistentListOf(
                DeviceDashboardUiModel(id = "main"),
                DeviceDashboardUiModel(id = "form"),
                DeviceDashboardUiModel(id = "markdown"),
            ),
            selectedId = "main",
            onDashboardSelected = {},
            onDeleteClicked = {},
        )
    }
}
