package io.github.openflocon.flocondesktop.features.network.ui.view.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.NetworkColumnsTypeUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.isFiltered
import io.github.openflocon.flocondesktop.features.network.ui.model.header.previewNetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.ui.view.NetworkItemColumnWidths
import io.github.openflocon.flocondesktop.features.network.ui.view.components.HeaderLabelItem
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun NetworkItemHeaderView(
    state: NetworkHeaderUiState,
    clickOnSort: (NetworkColumnsTypeUiModel, SortedByUiModel.Enabled) -> Unit,
    //onFilterChanged: () -> Unit,
    modifier: Modifier = Modifier,
    columnWidths: NetworkItemColumnWidths = NetworkItemColumnWidths(), // Default widths provided
    contentPadding: PaddingValues = PaddingValues(),
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
            isFiltered = state.time.isFiltered(),
            sortedBy = state.time.sortedBy,
            clickOnSort = {
                clickOnSort(NetworkColumnsTypeUiModel.Time, it)
            },
            clickOnFilter = {
            },
        )

        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.methodWidth),
            text = "Method",
            isFiltered = state.method.isFiltered(),
            sortedBy = state.method.sortedBy,
            clickOnSort = {
                clickOnSort(NetworkColumnsTypeUiModel.Method, it)
            },
            clickOnFilter = {
            },
        )

        // route & method, don't display the label
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            HeaderLabelItem(
                modifier = Modifier.weight(columnWidths.domainWeight),
                text = "Domain",
                labelAlignment = Alignment.CenterStart,
                isFiltered = state.domain.isFiltered(),
                sortedBy = state.domain.sortedBy,
                clickOnSort = {
                    clickOnSort(NetworkColumnsTypeUiModel.Domain, it)
                },
                clickOnFilter = {
                },
            )
            HeaderLabelItem(
                modifier = Modifier.weight(columnWidths.queryWeight),
                text = "Query",
                labelAlignment = Alignment.CenterStart,
                isFiltered = state.query.isFiltered(),
                sortedBy = state.query.sortedBy,
                clickOnSort = {
                    clickOnSort(NetworkColumnsTypeUiModel.Query, it)
                },
                clickOnFilter = {
                },
            )
        }

        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.statusCodeWidth),
            text = "Status",
            isFiltered = state.status.isFiltered(),
            sortedBy = state.status.sortedBy,
            clickOnSort = {
                clickOnSort(NetworkColumnsTypeUiModel.Status, it)
            },
            clickOnFilter = {
            },
        )
        HeaderLabelItem(
            modifier = Modifier.width(columnWidths.timeWidth),
            text = "Time",
            isFiltered = state.time.isFiltered(),
            sortedBy = state.time.sortedBy,
            clickOnSort = {
                clickOnSort(NetworkColumnsTypeUiModel.Time, it)
            },
            clickOnFilter = {
            }
        )
    }
}

@Composable
@Preview
private fun NetworkItemHeaderViewPreview() {
    FloconTheme {
        NetworkItemHeaderView(
            state = previewNetworkHeaderUiState(),
            clickOnSort = { _, _ -> },
        )
    }
}
