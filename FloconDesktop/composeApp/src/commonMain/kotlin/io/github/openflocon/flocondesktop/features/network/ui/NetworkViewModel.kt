package io.github.openflocon.flocondesktop.features.network.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.copyToClipboard
import io.github.openflocon.flocondesktop.features.network.domain.GenerateCurlCommandUseCase
import io.github.openflocon.flocondesktop.features.network.domain.ObserveHttpRequestsByIdUseCase
import io.github.openflocon.flocondesktop.features.network.domain.ObserveHttpRequestsUseCase
import io.github.openflocon.flocondesktop.features.network.domain.RemoveHttpRequestUseCase
import io.github.openflocon.flocondesktop.features.network.domain.RemoveHttpRequestsBeforeUseCase
import io.github.openflocon.flocondesktop.features.network.domain.ResetCurrentDeviceHttpRequestsUseCase
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.mapper.toDetailUi
import io.github.openflocon.flocondesktop.features.network.ui.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkJsonUi
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.NetworkColumnsTypeUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.NetworkMethodColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.NetworkStatusColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.NetworkTextColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.MethodFilterState
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.StatusFilterState
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterState
import io.github.openflocon.flocondesktop.features.network.ui.view.filters.MethodFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HeaderDelegate(
    private val closeableDelegate: CloseableDelegate,
    dispatcherProvider: DispatcherProvider,
) : CloseableScoped by closeableDelegate {

    data class Sorted(
        val column: NetworkColumnsTypeUiModel,
        val sort: SortedByUiModel.Enabled,
    )

    val sorted = MutableStateFlow<Sorted?>(null)

    val headerUiState = sorted
        .map { sorted ->
            buildHeaderValue(sorted = sorted)
        }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(coroutineScope, started = SharingStarted.WhileSubscribed(5_000), defaultHeaderValue())

    fun buildHeaderValue(sorted: Sorted?): NetworkHeaderUiState {
        return NetworkHeaderUiState(
            requestTime = NetworkTextColumnUiModel(
                sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.RequestTime }?.sort
                    ?: SortedByUiModel.None,
                filter = TextFilterState(activeFilters = emptyList(), isEnabled = false), // TODO
            ),
            method = NetworkMethodColumnUiModel(
                sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Method }?.sort
                    ?: SortedByUiModel.None,
                filter = MethodFilterState(isEnabled = false), // TODO
            ),
            domain = NetworkTextColumnUiModel(
                sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Domain }?.sort
                    ?: SortedByUiModel.None,
                filter = TextFilterState(activeFilters = emptyList(), isEnabled = false), // TODO
            ),
            query = NetworkTextColumnUiModel(
                sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Query }?.sort
                    ?: SortedByUiModel.None,
                filter = TextFilterState(activeFilters = emptyList(), isEnabled = false), // TODO
            ),
            status = NetworkStatusColumnUiModel(
                sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Status }?.sort
                    ?: SortedByUiModel.None,
                filter = StatusFilterState(isEnabled = false), // TODO
            ),
            time = NetworkTextColumnUiModel(
                sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Time }?.sort
                    ?: SortedByUiModel.None,
                filter = TextFilterState(activeFilters = emptyList(), isEnabled = false), // TODO
            ),
        )
    }

    fun defaultHeaderValue(): NetworkHeaderUiState {
        return NetworkHeaderUiState(
            requestTime = NetworkTextColumnUiModel.EMPTY,
            method = NetworkMethodColumnUiModel.EMPTY,
            domain = NetworkTextColumnUiModel.EMPTY,
            query = NetworkTextColumnUiModel.EMPTY,
            status = NetworkStatusColumnUiModel.EMPTY,
            time = NetworkTextColumnUiModel.EMPTY,
        )
    }

    fun onClickSort(type: NetworkColumnsTypeUiModel, sort: SortedByUiModel.Enabled) {
        val newValue = Sorted(
            column = type,
            sort = sort,
        )
        sorted.update {
            // click again to remove
            if (it == newValue) {
                null
            } else {
                newValue
            }
        }
    }
}

class NetworkViewModel(
    observeHttpRequestsUseCase: ObserveHttpRequestsUseCase,
    private val observeHttpRequestsByIdUseCase: ObserveHttpRequestsByIdUseCase,
    private val generateCurlCommandUseCase: GenerateCurlCommandUseCase,
    private val resetCurrentDeviceHttpRequestsUseCase: ResetCurrentDeviceHttpRequestsUseCase,
    private val removeHttpRequestsBeforeUseCase: RemoveHttpRequestsBeforeUseCase,
    private val removeHttpRequestUseCase: RemoveHttpRequestUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val headerDelegate: HeaderDelegate,
) : ViewModel(headerDelegate) {

    private val filterMethod = MethodFilter()

    private val contentState =
        MutableStateFlow(ContentUiState(selectedRequestId = null, detailJsons = emptySet()))

    private val filterUiState = MutableStateFlow(FilterUiState(query = ""))

    private val detailState: StateFlow<NetworkDetailViewState?> =
        contentState.map { it.selectedRequestId }
            .flatMapLatest { id ->
                if (id == null) {
                    flowOf(null)
                } else {
                    observeHttpRequestsByIdUseCase(id)
                        .distinctUntilChanged()
                        .map { it?.let { toDetailUi(it) } }
                }
            }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), null)

    private val filteredItems = combine(
        observeHttpRequestsUseCase().map { list -> list.map { Pair(it, toUi(it)) } }, // keep the domain for the filter
        filterUiState,
        headerDelegate.sorted,
    ) { items, filterState, sorted ->
        filterItems(items, filterState, sorted = sorted)

    }
        .distinctUntilChanged()

    val uiState = combine(
        filteredItems,
        contentState,
        detailState,
        filterUiState,
        headerDelegate.headerUiState,
    ) { items, content, detail, filter, header ->
        NetworkUiState(
            items = items,
            contentState = content,
            detailState = detail,
            filterState = filter,
            headerState = header,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NetworkUiState(
                items = emptyList(),
                detailState = detailState.value,
                contentState = contentState.value,
                filterState = filterUiState.value,
                headerState = headerDelegate.headerUiState.value,
            )
        )

    fun onAction(action: NetworkAction) {
        when (action) {
            is NetworkAction.SelectRequest -> onSelectRequest(action)
            NetworkAction.ClosePanel -> onClosePanel()
            is NetworkAction.CopyText -> onCopyText(action)
            NetworkAction.Reset -> onReset()
            is NetworkAction.CopyCUrl -> onCopyCUrl(action)
            is NetworkAction.CopyUrl -> onCopyUrl(action)
            is NetworkAction.Remove -> onRemove(action)
            is NetworkAction.RemoveLinesAbove -> onRemoveLinesAbove(action)
            is NetworkAction.FilterQuery -> onFilterQuery(action)
            is NetworkAction.FilterMethod -> {} //onFilterMethod(action)
            is NetworkAction.CloseJsonDetail -> onCloseJsonDetail(action)
            is NetworkAction.JsonDetail -> onJsonDetail(action)
            is NetworkAction.HeaderAction.ClickOnSort -> headerDelegate.onClickSort(
                type = action.type,
                sort = action.sort
            )
        }
    }

    private fun onSelectRequest(action: NetworkAction.SelectRequest) {
        contentState.update { state ->
            state.copy(
                selectedRequestId = if (state.selectedRequestId == action.id) {
                    null
                } else {
                    action.id
                }
            )
        }
    }

    private fun onClosePanel() {
        contentState.update { it.copy(selectedRequestId = null) }
    }

    private fun onCopyText(action: NetworkAction.CopyText) {
        copyToClipboard(action.text)
        feedbackDisplayer.displayMessage("copied")
    }

    private fun onJsonDetail(action: NetworkAction.JsonDetail) {
        contentState.update { state ->
            if (state.detailJsons.any { it.id == action.id })
                return

            state.copy(
                detailJsons = state.detailJsons + NetworkJsonUi(
                    id = action.id,
                    json = action.json
                )
            )
        }
    }

    private fun onCloseJsonDetail(action: NetworkAction.CloseJsonDetail) {
        contentState.update { state ->
            state.copy(detailJsons = state.detailJsons.filterNot { it.id == action.id }.toSet())
        }
    }

    private fun onReset() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            resetCurrentDeviceHttpRequestsUseCase()
        }
    }

    private fun onCopyCUrl(action: NetworkAction.CopyCUrl) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val domainModel = observeHttpRequestsByIdUseCase(action.item.uuid).firstOrNull()
                ?: return@launch
            val curl = generateCurlCommandUseCase(domainModel)
            copyToClipboard(curl)
        }
    }

    private fun onCopyUrl(action: NetworkAction.CopyUrl) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val domainModel = observeHttpRequestsByIdUseCase(action.item.uuid).firstOrNull()
                ?: return@launch
            copyToClipboard(domainModel.url)
        }
    }

    private fun onRemove(action: NetworkAction.Remove) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            removeHttpRequestUseCase(requestId = action.item.uuid)
        }
    }

    private fun onRemoveLinesAbove(action: NetworkAction.RemoveLinesAbove) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            removeHttpRequestsBeforeUseCase(requestId = action.item.uuid)
        }
    }

    private fun onFilterQuery(action: NetworkAction.FilterQuery) {
        filterUiState.update { state ->
            state.copy(query = action.query)
        }
    }
}


private fun filterItems(
    items: List<Pair<FloconHttpRequestDomainModel, NetworkItemViewState>>,
    filterState: FilterUiState,
    sorted: HeaderDelegate.Sorted?,
): List<NetworkItemViewState> {
    val filteredItems = if (filterState.query.isNotEmpty())
        items.filter { it.second.contains(filterState.query) }
    else items

    val sortedItems = if (sorted != null) {
        when (sorted.column) {
            NetworkColumnsTypeUiModel.RequestTime -> {
                when (sorted.sort) {
                    SortedByUiModel.Enabled.Ascending -> filteredItems.sortedBy { it.first.request.startTime }
                    SortedByUiModel.Enabled.Descending -> filteredItems.sortedByDescending { it.first.request.startTime }
                }
            }

            NetworkColumnsTypeUiModel.Method -> {
                when (sorted.sort) {
                    SortedByUiModel.Enabled.Ascending -> filteredItems.sortedBy { it.second.method.text }
                    SortedByUiModel.Enabled.Descending -> filteredItems.sortedByDescending { it.second.method.text }
                }
            }

            NetworkColumnsTypeUiModel.Domain -> {
                when (sorted.sort) {
                    SortedByUiModel.Enabled.Ascending -> filteredItems.sortedBy { it.second.domain }
                    SortedByUiModel.Enabled.Descending -> filteredItems.sortedByDescending { it.second.domain }
                }
            }

            NetworkColumnsTypeUiModel.Query -> {
                when (sorted.sort) {
                    SortedByUiModel.Enabled.Ascending -> filteredItems.sortedBy { it.second.type.text }
                    SortedByUiModel.Enabled.Descending -> filteredItems.sortedByDescending { it.second.type.text }
                }
            }

            NetworkColumnsTypeUiModel.Status -> when (sorted.sort) {
                SortedByUiModel.Enabled.Ascending -> filteredItems.sortedBy { it.second.status.text }
                SortedByUiModel.Enabled.Descending -> filteredItems.sortedByDescending { it.second.status.text }
            }

            NetworkColumnsTypeUiModel.Time -> {
                when (sorted.sort) {
                    SortedByUiModel.Enabled.Ascending -> filteredItems.sortedBy { it.first.durationMs }
                    SortedByUiModel.Enabled.Descending -> filteredItems.sortedByDescending { it.first.durationMs }
                }
            }
        }
    } else {
        filteredItems
    }


    return sortedItems.map { it.second }
}

