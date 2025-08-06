package io.github.openflocon.flocondesktop.features.network.ui.view.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.ui.view.NetworkItemColumnWidths
import io.github.openflocon.flocondesktop.features.network.ui.view.components.HeaderLabelItem
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class NetworkColumnsTypeUiModel {
    RequestTime,
    Method,
    Domain,
    Query,
    Status,
    Time,
}

interface FilterState {
    val isActive: Boolean
}





@Composable
fun NetworkItemHeaderView(
    modifier: Modifier = Modifier,
    state: NetworkHeaderUiState,
    columnWidths: NetworkItemColumnWidths = NetworkItemColumnWidths(), // Default widths provided
    contentPadding: PaddingValues = PaddingValues(),
    clickOnSort: (NetworkColumnsTypeUiModel, SortedByUiModel.Enabled) -> Unit,
    clickOnFilter: (NetworkColumnsTypeUiModel) -> Unit,
) {
    Row(
        modifier =
            modifier
                .background(FloconTheme.colorPalette.panel)
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .padding(contentPadding),

        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        // Date - Fixed width from data class
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.dateWidth),
            text = "Request Time",
            isFiltered = state.time.filter,
            clickOnSort = {
                clickOnSort(NetworkColumnsTypeUiModel.Time)
            }
        )

        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.methodWidth),
            text = "Method",
        )

        // route & method, don't display the label
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            HeaderLabelItem(
                modifier = Modifier.weight(columnWidths.domainWeight),
                text = "Domain",
                labelAlignment = Alignment.CenterStart,
            )
            HeaderLabelItem(
                modifier = Modifier.weight(columnWidths.queryWeight),
                text = "Query",
                labelAlignment = Alignment.CenterStart,
            )
        }

        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.statusCodeWidth),
            text = "Status",
        )
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.timeWidth),
            text = "Time",
        )
    }
}

@Composable
@Preview
private fun NetworkItemHeaderViewPreview() {
    FloconTheme {
        NetworkItemHeaderView()
    }
}
