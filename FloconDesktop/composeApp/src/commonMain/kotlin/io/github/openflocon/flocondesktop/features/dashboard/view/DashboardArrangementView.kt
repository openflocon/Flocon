package io.github.openflocon.flocondesktop.features.dashboard.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_auto
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardArrangement
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DashboardArrangementView(
    arrangement: DashboardArrangement,
    onArrangementClicked: (DashboardArrangement) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = { expanded = true },
        anchorContent = {
            FloconButton(
                onClick = { expanded = true },
                containerColor = FloconTheme.colors.bg200
            ) {
                Text(
                    text = when (arrangement) {
                        is DashboardArrangement.Adaptive -> "Items per row: Auto"
                        is DashboardArrangement.Fixed -> "Items per row: ${arrangement.itemsPerRow}"
                    },
                    style = FloconTheme.typography.bodySmall
                )
            }
        },
        modifier = modifier
    ) {
        FloconDropdownMenuItem(
            text = stringResource(Res.string.general_auto),
            onClick = {
                onArrangementClicked(DashboardArrangement.Adaptive)
                expanded = false
            },
        )
        repeat(5) { itemCount ->
            FloconDropdownMenuItem(
                text = "${itemCount + 1}",
                onClick = {
                    onArrangementClicked(DashboardArrangement.Fixed(itemsPerRow = itemCount + 1))
                    expanded = false
                }
            )
        }
    }
}
