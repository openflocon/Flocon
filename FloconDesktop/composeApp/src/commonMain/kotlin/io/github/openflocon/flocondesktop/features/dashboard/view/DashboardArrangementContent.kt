package io.github.openflocon.flocondesktop.features.dashboard.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_auto
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardArrangement
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSegmentedTextToggle
import kotlinx.collections.immutable.toImmutableList
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
    val options = remember(auto) {
        (listOf(auto) + (1..MaxItemsPerRow).map { it.toString() }).toImmutableList()
    }

    FloconSegmentedTextToggle(
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
