package io.github.openflocon.flocondesktop.features.network.list.view.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemColumnWidths
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.list.model.header.OnFilterAction
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.NetworkColumnsTypeUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.isFiltered
import io.github.openflocon.flocondesktop.features.network.list.model.header.previewNetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.list.view.components.HeaderDropdown
import io.github.openflocon.flocondesktop.features.network.list.view.filters.MethodFilterDropdownContent
import io.github.openflocon.flocondesktop.features.network.list.view.filters.TextFilterDropdownContent
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NetworkItemHeaderView(
    state: NetworkHeaderUiState,
    clickOnSort: (NetworkColumnsTypeUiModel, SortedByUiModel.Enabled) -> Unit,
    onFilterAction: (OnFilterAction) -> Unit,
    modifier: Modifier = Modifier,
    columnWidths: NetworkItemColumnWidths = NetworkItemColumnWidths(), // Default widths provided
    contentPadding: PaddingValues = PaddingValues(),
) {
    Row(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        HeaderDropdown(
            label = "Request Time",
            filtered = state.requestTime.isFiltered(),
            sortedBy = state.requestTime.sortedBy,
            onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.RequestTime, it) },
            modifier = Modifier.width(columnWidths.dateWidth),
        ) {
            TextFilterDropdownContent(
                filterState = state.requestTime.filter,
                textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.RequestTime, it)) },
                modifier = Modifier.widthIn(min = 300.dp),
            )
        }
        HeaderDropdown(
            label = "Method",
            filtered = state.method.isFiltered(),
            sortedBy = state.method.sortedBy,
            onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Method, it) },
            modifier = Modifier.width(columnWidths.methodWidth),
        ) {
            MethodFilterDropdownContent(
                filterState = state.method.filter,
                onItemClicked = { onFilterAction(OnFilterAction.ClickOnMethod(it)) },
            )
        }
        HeaderDropdown(
            label = "Domain",
            filtered = state.domain.isFiltered(),
            sortedBy = state.domain.sortedBy,
            onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Domain, it) },
            labelAlignment = Alignment.CenterStart,
            modifier = Modifier.weight(columnWidths.domainWeight),
        ) {
            TextFilterDropdownContent(
                filterState = state.domain.filter,
                textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Domain, it)) },
                modifier = Modifier.widthIn(min = 300.dp),
            )
        }
        HeaderDropdown(
            label = "Query",
            filtered = state.query.isFiltered(),
            sortedBy = state.query.sortedBy,
            onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Query, it) },
            labelAlignment = Alignment.CenterStart,
            modifier = Modifier.weight(columnWidths.queryWeight),
        ) {
            TextFilterDropdownContent(
                filterState = state.query.filter,
                textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Query, it)) },
                modifier = Modifier.widthIn(min = 300.dp),
            )
        }
        HeaderDropdown(
            label = "Status",
            filtered = state.status.isFiltered(),
            sortedBy = state.status.sortedBy,
            onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Status, it) },
            modifier = Modifier.width(columnWidths.statusCodeWidth),
        ) {
            TextFilterDropdownContent(
                filterState = state.status.filter,
                textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Status, it)) },
                modifier = Modifier.widthIn(min = 300.dp),
            )
        }
        HeaderDropdown(
            label = "Time",
            filtered = state.time.isFiltered(),
            sortedBy = state.time.sortedBy,
            onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Time, it) },
            modifier = Modifier.width(columnWidths.timeWidth),
        ) {
            TextFilterDropdownContent(
                filterState = state.time.filter,
                textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Time, it)) },
                modifier = Modifier.widthIn(min = 300.dp),
            )
        }
    }
}

@Composable
@Preview
private fun NetworkItemHeaderViewPreview() {
    FloconTheme {
        NetworkItemHeaderView(
            state = previewNetworkHeaderUiState(),
            clickOnSort = { _, _ -> },
            onFilterAction = {},
        )
    }
}
