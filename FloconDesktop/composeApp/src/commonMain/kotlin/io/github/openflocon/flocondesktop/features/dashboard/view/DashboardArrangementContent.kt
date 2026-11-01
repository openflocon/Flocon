package io.github.openflocon.flocondesktop.features.dashboard.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.material.icons.outlined.ViewModule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.dashboard_items_per_row
import flocondesktop.composeapp.generated.resources.general_auto
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardArrangement
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSegmentedIconOption
import io.github.openflocon.library.designsystem.components.FloconSegmentedIconToggle
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

private const val MaxItemsPerRow = 3

/** Auto, then 1 to [MaxItemsPerRow] items per row. */
@Composable
internal fun DashboardArrangementContent(
    arrangement: DashboardArrangement,
    onArrangementClicked: (DashboardArrangement) -> Unit,
    modifier: Modifier = Modifier,
) {
    val auto = stringResource(Res.string.general_auto)
    val one = stringResource(Res.string.dashboard_items_per_row, 1)
    val two = stringResource(Res.string.dashboard_items_per_row, 2)
    val three = stringResource(Res.string.dashboard_items_per_row, 3)
    val options = remember(auto, one, two, three) {
        persistentListOf(
            FloconSegmentedIconOption(icon = Icons.Outlined.GridView, tooltip = auto),
            FloconSegmentedIconOption(icon = Icons.Outlined.ViewAgenda, tooltip = one),
            FloconSegmentedIconOption(icon = Icons.Outlined.ViewColumn, tooltip = two),
            FloconSegmentedIconOption(icon = Icons.Outlined.ViewModule, tooltip = three),
        )
    }

    FloconSegmentedIconToggle(
        modifier = modifier,
        options = options,
        selectedIndex = when (arrangement) {
            is DashboardArrangement.Adaptive -> 0
            is DashboardArrangement.Fixed -> arrangement.itemsPerRow.coerceIn(1, MaxItemsPerRow)
        },
        onSelect = { index ->
            onArrangementClicked(
                if (index == 0) {
                    DashboardArrangement.Adaptive
                } else {
                    DashboardArrangement.Fixed(itemsPerRow = index)
                }
            )
        },
    )
}

@Preview
@Composable
private fun DashboardArrangementContentPreview() {
    FloconTheme {
        DashboardArrangementContent(
            arrangement = DashboardArrangement.Adaptive,
            onArrangementClicked = {},
        )
    }
}
