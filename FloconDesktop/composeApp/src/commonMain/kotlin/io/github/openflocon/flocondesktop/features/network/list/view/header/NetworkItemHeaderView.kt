package io.github.openflocon.flocondesktop.features.network.list.view.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.network_header_host
import flocondesktop.composeapp.generated.resources.network_header_method
import flocondesktop.composeapp.generated.resources.network_header_request
import flocondesktop.composeapp.generated.resources.network_header_request_time
import flocondesktop.composeapp.generated.resources.network_header_status
import flocondesktop.composeapp.generated.resources.network_header_time
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemColumnWidths
import io.github.openflocon.flocondesktop.features.network.list.model.ResizableColumn
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.list.model.header.OnFilterAction
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.NetworkColumnsTypeUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.isFiltered
import io.github.openflocon.flocondesktop.features.network.list.model.header.previewNetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.list.view.components.ColumnResizeHandle
import io.github.openflocon.flocondesktop.features.network.list.view.components.HeaderDropdown
import io.github.openflocon.flocondesktop.features.network.list.view.filters.MethodFilterDropdownContent
import io.github.openflocon.flocondesktop.features.network.list.view.filters.TextFilterDropdownContent
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource
import androidx.compose.desktop.ui.tooling.preview.Preview

@Composable
fun NetworkItemHeaderView(
    state: NetworkHeaderUiState,
    clickOnSort: (NetworkColumnsTypeUiModel, SortedByUiModel.Enabled) -> Unit,
    onFilterAction: (OnFilterAction) -> Unit,
    modifier: Modifier = Modifier,
    columnWidths: NetworkItemColumnWidths = NetworkItemColumnWidths(), // Default widths provided
    contentPadding: PaddingValues = PaddingValues(),
) {
    val density = LocalDensity.current
    Row(
        modifier = modifier
            .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.compact)
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.Bottom,
    ) {
        ResizableHeaderCell(
            modifier = Modifier.width(columnWidths.dateWidth),
            onResize = { columnWidths.resize(ResizableColumn.Date, it) },
        ) {
            HeaderDropdown(
                label = stringResource(Res.string.network_header_request_time),
                filtered = state.requestTime.isFiltered(),
                sortedBy = state.requestTime.sortedBy,
                onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.RequestTime, it) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextFilterDropdownContent(
                    filterState = state.requestTime.filter,
                    textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.RequestTime, it)) },
                )
            }
        }
        ResizableHeaderCell(
            modifier = Modifier.width(columnWidths.methodWidth),
            onResize = { columnWidths.resize(ResizableColumn.Method, it) },
        ) {
            HeaderDropdown(
                label = stringResource(Res.string.network_header_method),
                filtered = state.method.isFiltered(),
                sortedBy = state.method.sortedBy,
                onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Method, it) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                MethodFilterDropdownContent(
                    filterState = state.method.filter,
                    onItemClicked = { onFilterAction(OnFilterAction.ClickOnMethod(it)) },
                )
            }
        }
        ResizableHeaderCell(
            modifier = Modifier.width(columnWidths.domainWidth),
            onResize = { columnWidths.resize(ResizableColumn.Domain, it) },
        ) {
            HeaderDropdown(
                label = stringResource(Res.string.network_header_host),
                filtered = state.domain.isFiltered(),
                sortedBy = state.domain.sortedBy,
                onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Domain, it) },
                labelAlignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextFilterDropdownContent(
                    filterState = state.domain.filter,
                    textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Domain, it)) },
                )
            }
        }
        // the request column takes the remaining space: its right border resizes the status column
        ResizableHeaderCell(
            modifier = Modifier
                .weight(1f)
                .onSizeChanged { columnWidths.requestWidth = with(density) { it.width.toDp() } },
            onResize = { columnWidths.resize(ResizableColumn.Status, -it) },
        ) {
            HeaderDropdown(
                label = stringResource(Res.string.network_header_request),
                filtered = state.query.isFiltered(),
                sortedBy = state.query.sortedBy,
                onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Query, it) },
                labelAlignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextFilterDropdownContent(
                    filterState = state.query.filter,
                    textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Query, it)) },
                )
            }
        }
        ResizableHeaderCell(
            modifier = Modifier.width(columnWidths.statusCodeWidth),
            onResize = { columnWidths.resize(ResizableColumn.Status, it) },
        ) {
            HeaderDropdown(
                label = stringResource(Res.string.network_header_status),
                filtered = state.status.isFiltered(),
                sortedBy = state.status.sortedBy,
                onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Status, it) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextFilterDropdownContent(
                    filterState = state.status.filter,
                    textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Status, it)) },
                )
            }
        }
        ResizableHeaderCell(
            modifier = Modifier.width(columnWidths.timeWidth),
            onResize = { columnWidths.resize(ResizableColumn.Time, it) },
        ) {
            HeaderDropdown(
                label = stringResource(Res.string.network_header_time),
                filtered = state.time.isFiltered(),
                sortedBy = state.time.sortedBy,
                onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Time, it) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextFilterDropdownContent(
                    filterState = state.time.filter,
                    textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Time, it)) },
                )
            }
        }
    }
}

@Composable
private fun ResizableHeaderCell(
    onResize: (Dp) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        // sized to the cell, so the handle's fillMaxHeight doesn't stretch the header
        Box(
            modifier = Modifier.matchParentSize(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            ColumnResizeHandle(onResize = onResize)
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
