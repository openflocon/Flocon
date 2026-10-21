package io.github.openflocon.flocondesktop.features.network.list.view.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.network_header_host
import flocondesktop.composeapp.generated.resources.network_header_method
import flocondesktop.composeapp.generated.resources.network_header_request
import flocondesktop.composeapp.generated.resources.network_header_request_time
import flocondesktop.composeapp.generated.resources.network_header_status
import flocondesktop.composeapp.generated.resources.network_header_time
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
    Row(
        modifier = modifier
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.compact)
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
        verticalAlignment = Alignment.Bottom,
    ) {
        HeaderDropdown(
            label = stringResource(Res.string.network_header_request_time),
            filtered = state.requestTime.isFiltered(),
            sortedBy = state.requestTime.sortedBy,
            onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.RequestTime, it) },
            modifier = Modifier.width(columnWidths.dateWidth),
        ) {
            TextFilterDropdownContent(
                filterState = state.requestTime.filter,
                textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.RequestTime, it)) },
            )
        }
        HeaderDropdown(
            label = stringResource(Res.string.network_header_method),
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
            label = stringResource(Res.string.network_header_host),
            filtered = state.domain.isFiltered(),
            sortedBy = state.domain.sortedBy,
            onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Domain, it) },
            labelAlignment = Alignment.CenterStart,
            modifier = Modifier.weight(columnWidths.domainWeight),
        ) {
            TextFilterDropdownContent(
                filterState = state.domain.filter,
                textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Domain, it)) },
            )
        }
        HeaderDropdown(
            label = stringResource(Res.string.network_header_request),
            filtered = state.query.isFiltered(),
            sortedBy = state.query.sortedBy,
            onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Query, it) },
            labelAlignment = Alignment.CenterStart,
            modifier = Modifier.weight(columnWidths.queryWeight),
        ) {
            TextFilterDropdownContent(
                filterState = state.query.filter,
                textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Query, it)) },
            )
        }
        HeaderDropdown(
            label = stringResource(Res.string.network_header_status),
            filtered = state.status.isFiltered(),
            sortedBy = state.status.sortedBy,
            onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Status, it) },
            modifier = Modifier.width(columnWidths.statusCodeWidth),
        ) {
            TextFilterDropdownContent(
                filterState = state.status.filter,
                textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Status, it)) },
            )
        }
        HeaderDropdown(
            label = stringResource(Res.string.network_header_time),
            filtered = state.time.isFiltered(),
            sortedBy = state.time.sortedBy,
            onClickSort = { clickOnSort(NetworkColumnsTypeUiModel.Time, it) },
            modifier = Modifier.width(columnWidths.timeWidth),
        ) {
            TextFilterDropdownContent(
                filterState = state.time.filter,
                textFilterAction = { onFilterAction(OnFilterAction.TextFilter(NetworkTextFilterColumns.Time, it)) },
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
