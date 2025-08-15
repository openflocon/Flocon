package io.github.openflocon.flocondesktop.features.network.view.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.features.network.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.model.header.OnFilterAction
import io.github.openflocon.flocondesktop.features.network.model.header.columns.NetworkColumnsTypeUiModel
import io.github.openflocon.flocondesktop.features.network.model.header.columns.base.isFiltered
import io.github.openflocon.flocondesktop.features.network.model.header.previewNetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.view.NetworkItemColumnWidths
import io.github.openflocon.flocondesktop.features.network.view.components.HeaderLabelItem
import io.github.openflocon.flocondesktop.features.network.view.filters.MethodFilterDropdown
import io.github.openflocon.flocondesktop.features.network.view.filters.TextFilterDropdown
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
        modifier =
        modifier
            .background(FloconTheme.colorPalette.panel)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .padding(contentPadding),

        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(modifier = Modifier.width(columnWidths.dateWidth)) {
            var isDropdownExpanded by remember { mutableStateOf(false) }
            HeaderLabelItem(
                modifier = Modifier.fillMaxWidth(),
                text = "Request Time",
                isFiltered = state.requestTime.isFiltered(),
                sortedBy = state.requestTime.sortedBy,
                clickOnSort = {
                    clickOnSort(NetworkColumnsTypeUiModel.RequestTime, it)
                },
                clickOnFilter = {
                    isDropdownExpanded = true
                },
            )
            TextFilterDropdown(
                expanded = isDropdownExpanded,
                onDismissRequest = {
                    isDropdownExpanded = false
                },
                filterState = state.requestTime.filter,
                textFilterAction = {
                    onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.RequestTime, it))
                },
            )
        }

        Column(modifier = Modifier.width(columnWidths.methodWidth)) {
            var isDropdownExpanded by remember { mutableStateOf(false) }
            HeaderLabelItem(
                modifier = Modifier.fillMaxWidth(),
                text = "Method",
                isFiltered = state.method.isFiltered(),
                sortedBy = state.method.sortedBy,
                clickOnSort = {
                    clickOnSort(NetworkColumnsTypeUiModel.Method, it)
                },
                clickOnFilter = {
                    isDropdownExpanded = true
                },
            )
            MethodFilterDropdown(
                expanded = isDropdownExpanded,
                onDismissRequest = {
                    isDropdownExpanded = false
                },
                filterState = state.method.filter,
                onItemClicked = {
                    onFilterAction(OnFilterAction.ClickOnMethod(it))
                },
            )
        }

        // route & method, don't display the label
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(columnWidths.domainWeight)) {
                var isDropdownExpanded by remember { mutableStateOf(false) }
                HeaderLabelItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Domain",
                    labelAlignment = Alignment.CenterStart,
                    isFiltered = state.domain.isFiltered(),
                    sortedBy = state.domain.sortedBy,
                    clickOnSort = {
                        clickOnSort(NetworkColumnsTypeUiModel.Domain, it)
                    },
                    clickOnFilter = {
                        isDropdownExpanded = true
                    },
                )
                TextFilterDropdown(
                    expanded = isDropdownExpanded,
                    filterState = state.domain.filter,
                    onDismissRequest = {
                        isDropdownExpanded = false
                    },
                    textFilterAction = {
                        onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Domain, it))
                    },
                )
            }
            Column(modifier = Modifier.weight(columnWidths.queryWeight)) {
                var isDropdownExpanded by remember { mutableStateOf(false) }
                HeaderLabelItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Query",
                    labelAlignment = Alignment.CenterStart,
                    isFiltered = state.query.isFiltered(),
                    sortedBy = state.query.sortedBy,
                    clickOnSort = {
                        clickOnSort(NetworkColumnsTypeUiModel.Query, it)
                    },
                    clickOnFilter = {
                        isDropdownExpanded = true
                    },
                )
                TextFilterDropdown(
                    expanded = isDropdownExpanded,
                    filterState = state.query.filter,
                    onDismissRequest = {
                        isDropdownExpanded = false
                    },
                    textFilterAction = {
                        onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Query, it))
                    },
                )
            }
        }

        Column(modifier = Modifier.width(columnWidths.statusCodeWidth)) {
            var isDropdownExpanded by remember { mutableStateOf(false) }
            HeaderLabelItem(
                modifier = Modifier.fillMaxWidth(),
                text = "Status",
                isFiltered = state.status.isFiltered(),
                sortedBy = state.status.sortedBy,
                clickOnSort = {
                    clickOnSort(NetworkColumnsTypeUiModel.Status, it)
                },
                clickOnFilter = {
                    isDropdownExpanded = true
                },
            )
            TextFilterDropdown(
                expanded = isDropdownExpanded,
                filterState = state.status.filter,
                onDismissRequest = {
                    isDropdownExpanded = false
                },
                textFilterAction = {
                    onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Status, it))
                },
            )
        }

        Column(modifier = Modifier.width(columnWidths.timeWidth)) {
            var isDropdownExpanded by remember { mutableStateOf(false) }
            HeaderLabelItem(
                modifier = Modifier.fillMaxWidth(),
                text = "Time",
                isFiltered = state.time.isFiltered(),
                sortedBy = state.time.sortedBy,
                clickOnSort = {
                    clickOnSort(NetworkColumnsTypeUiModel.Time, it)
                },
                clickOnFilter = {
                    isDropdownExpanded = true
                },
            )
            TextFilterDropdown(
                expanded = isDropdownExpanded,
                filterState = state.time.filter,
                onDismissRequest = {
                    isDropdownExpanded = false
                },
                textFilterAction = {
                    onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Time, it))
                },
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
