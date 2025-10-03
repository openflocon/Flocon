package io.github.openflocon.flocondesktop.features.network.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel.Filters
import io.github.openflocon.domain.network.models.NetworkSortDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.network.usecase.DecodeJwtTokenUseCase
import io.github.openflocon.domain.network.usecase.ExportNetworkCallsToCsvUseCase
import io.github.openflocon.domain.network.usecase.GenerateCurlCommandUseCase
import io.github.openflocon.domain.network.usecase.ObserveNetworkRequestsByIdUseCase
import io.github.openflocon.domain.network.usecase.ObserveNetworkRequestsUseCase
import io.github.openflocon.domain.network.usecase.RemoveHttpRequestsBeforeUseCase
import io.github.openflocon.domain.network.usecase.RemoveNetworkRequestUseCase
import io.github.openflocon.domain.network.usecase.RemoveOldSessionsNetworkRequestUseCase
import io.github.openflocon.domain.network.usecase.ResetCurrentDeviceHttpRequestsUseCase
import io.github.openflocon.domain.network.usecase.badquality.ObserveAllNetworkBadQualitiesUseCase
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkMocksUseCase
import io.github.openflocon.flocondesktop.features.network.body.model.ContentUiState
import io.github.openflocon.flocondesktop.features.network.body.model.MockDisplayed
import io.github.openflocon.flocondesktop.features.network.detail.mapper.toDetailUi
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.list.delegate.HeaderDelegate
import io.github.openflocon.flocondesktop.features.network.list.mapper.toDomain
import io.github.openflocon.flocondesktop.features.network.list.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkUiState
import io.github.openflocon.flocondesktop.features.network.list.model.TopBarUiState
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.TextFilterStateUiModel
import io.github.openflocon.flocondesktop.features.network.list.processor.FilterNetworkItemsProcessor
import io.github.openflocon.flocondesktop.features.network.model.NetworkBodyDetailUi
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.Flow
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
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NetworkViewModel(
    observeNetworkRequestsUseCase: ObserveNetworkRequestsUseCase,
    private val observeNetworkRequestsByIdUseCase: ObserveNetworkRequestsByIdUseCase,
    private val generateCurlCommandUseCase: GenerateCurlCommandUseCase,
    private val resetCurrentDeviceHttpRequestsUseCase: ResetCurrentDeviceHttpRequestsUseCase,
    private val removeHttpRequestsBeforeUseCase: RemoveHttpRequestsBeforeUseCase,
    private val removeNetworkRequestUseCase: RemoveNetworkRequestUseCase,
    private val mocksUseCase: ObserveNetworkMocksUseCase,
    private val badNetworkUseCase: ObserveAllNetworkBadQualitiesUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val headerDelegate: HeaderDelegate,
    private val filterNetworkItemsProcessor: FilterNetworkItemsProcessor,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val exportNetworkCallsToCsv: ExportNetworkCallsToCsvUseCase,
    private val decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
    private val removeOldSessionsNetworkRequestUseCase: RemoveOldSessionsNetworkRequestUseCase,
) : ViewModel(headerDelegate) {

    private val contentState = MutableStateFlow(
        ContentUiState(
            selectedRequestId = null,
            detailJsons = emptySet(),
            mocksDisplayed = null,
            badNetworkQualityDisplayed = false,
            invertList = false,
            autoScroll = false
        ),
    )

    private val _filterText = mutableStateOf("")
    val filterText: State<String> = _filterText

    private val filterUiState = combine(
        mocksUseCase().map { it.any(MockNetworkDomainModel::isEnabled) }.distinctUntilChanged(),
        badNetworkUseCase().map { it.any(BadQualityConfigDomainModel::isEnabled) }
            .distinctUntilChanged()
    ) { mockEnabled, badNetworkEnabled ->
        TopBarUiState(
            hasBadNetwork = badNetworkEnabled,
            hasMocks = mockEnabled,
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000),
        TopBarUiState(hasBadNetwork = false, hasMocks = false)
    )

    private val detailState: StateFlow<NetworkDetailViewState?> =
        contentState.map { it.selectedRequestId }
            .flatMapLatest { id ->
                if (id == null) {
                    flowOf(null)
                } else {
                    observeNetworkRequestsByIdUseCase(id)
                        .distinctUntilChanged()
                        .map { it?.let { toDetailUi(it) } }
                }
            }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), null)

    private val filter = combines(
        snapshotFlow { _filterText.value }.map { it.takeIf { it.isNotBlank() } },
        headerDelegate.textFiltersState.map { it.toDomain() }
    ).map { (textFilters, filterOnAllColumns) ->
        NetworkFilterDomainModel(
            filterOnAllColumns = textFilters,
            filters = filterOnAllColumns,
        )
    }

    private val sortAndFilter = combines(
        headerDelegate.sorted.map { it?.toDomain() }.distinctUntilChanged(),
        filter,
    ).distinctUntilChanged()

    private val networkItems: Flow<List<FloconNetworkCallDomainModel>> =
        sortAndFilter.flatMapLatest { (sorted, filter) ->
            observeNetworkRequestsUseCase(
                sortedBy = sorted,
                filter = filter,
            )
        }

    private val items = combines(
        networkItems,
        observeCurrentDeviceIdAndPackageNameUseCase(),
    ).mapLatest { (list, deviceIdAndPackageName) ->
        list.map { networkCall ->
            networkCall to toUi(
                networkCall = networkCall,
                deviceIdAndPackageName = deviceIdAndPackageName
            )
        } // keep the domain for the filter
    }

    data class FilterConfig(
        val filterState: TopBarUiState,
        val allowedMethods: List<NetworkMethodUi>,
        val textFilters: Map<NetworkTextFilterColumns, TextFilterStateUiModel>,
    )

    private val filterConfig = combine(
        filterUiState,
        headerDelegate.allowedMethods(),
        headerDelegate.textFiltersState,
    ) { filterState, allowedMethods, textFilters ->
        FilterConfig(
            filterState = filterState,
            allowedMethods = allowedMethods,
            textFilters = textFilters,
        )
    }
        .distinctUntilChanged()

    private val filteredItems: Flow<List<NetworkItemViewState>> = combine(
        items,
        filterConfig,
    ) { items, config ->
        filterNetworkItemsProcessor(
            items = items,
            allowedMethods = config.allowedMethods,
        )
    }
        .flowOn(dispatcherProvider.viewModel.limitedParallelism(1))
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
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NetworkUiState(
                items = emptyList(),
                detailState = detailState.value,
                contentState = contentState.value,
                filterState = filterUiState.value,
                headerState = headerDelegate.headerUiState.value,
            ),
        )

    fun onAction(action: NetworkAction) {
        when (action) {
            is NetworkAction.SelectRequest -> onSelectRequest(action)
            is NetworkAction.ClosePanel -> onClosePanel()
            is NetworkAction.CopyText -> onCopyText(action)
            is NetworkAction.Reset -> onReset()
            is NetworkAction.OpenMocks -> openMocks(callId = null)
            is NetworkAction.CreateMock -> {
                openMocks(callId = action.item.uuid)
            }

            is NetworkAction.OpenBadNetworkQuality -> openBadNetworkQuality()
            is NetworkAction.CloseBadNetworkQuality -> closeBadNetworkQuality()
            is NetworkAction.CloseMocks -> closeMocks()
            is NetworkAction.CopyCUrl -> onCopyCUrl(action)
            is NetworkAction.CopyUrl -> onCopyUrl(action)
            is NetworkAction.Remove -> onRemove(action)
            is NetworkAction.RemoveLinesAbove -> onRemoveLinesAbove(action)
            is NetworkAction.FilterQuery -> onFilterQuery(action)
            is NetworkAction.CloseJsonDetail -> onCloseJsonDetail(action)
            is NetworkAction.JsonDetail -> onJsonDetail(action)
            is NetworkAction.DisplayBearerJwt -> displayBearerJwt(action.token)
            is NetworkAction.ExportCsv -> onExportCsv()
            is NetworkAction.HeaderAction.ClickOnSort -> headerDelegate.onClickSort(
                type = action.type,
                sort = action.sort,
            )

            is NetworkAction.HeaderAction.FilterAction -> headerDelegate.onFilterAction(
                action = action.action,
            )

            is NetworkAction.InvertList -> onInvertList(action)
            NetworkAction.ToggleAutoScroll -> onAutoScroll()
            NetworkAction.ClearOldSession -> onClearSession()
            NetworkAction.Down -> onDown()
            NetworkAction.Up -> onUp()
        }
    }

    private fun onUp() {
        val state = uiState.value
        val selectedItem = state.contentState.selectedRequestId
        val index = state.items.indexOfFirst { it.uuid == selectedItem }
            .takeIf { it != -1 }
            ?: return
        val nextRequest = state.items.getOrNull(
            if (state.contentState.invertList)
                index + 1
            else
                index - 1
        ) ?: return

        contentState.update { it.copy(selectedRequestId = nextRequest.uuid) }
    }

    private fun onDown() {
        val state = uiState.value
        val selectedItem = state.contentState.selectedRequestId
        val index = state.items.indexOfFirst { it.uuid == selectedItem }
            .takeIf { it != -1 }
            ?: return
        val nextRequest = state.items.getOrNull(
            if (state.contentState.invertList)
                index - 1
            else
                index + 1
        ) ?: return

        contentState.update { it.copy(selectedRequestId = nextRequest.uuid) }
    }

    private fun onClearSession() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            removeOldSessionsNetworkRequestUseCase()
        }
    }

    private fun onAutoScroll() {
        contentState.update { it.copy(autoScroll = !it.autoScroll) }
    }

    private fun onInvertList(action: NetworkAction.InvertList) {
        contentState.update { it.copy(invertList = action.value) }
    }

    private fun displayBearerJwt(token: String) {
        decodeJwtTokenUseCase(token)?.let {
            onJsonDetail(NetworkAction.JsonDetail(id = token, json = it))
        }
    }

    private fun onSelectRequest(action: NetworkAction.SelectRequest) {
        contentState.update { state ->
            state.copy(
                selectedRequestId = if (state.selectedRequestId == action.id) {
                    null
                } else {
                    action.id
                },
            )
        }
    }

    private fun openMocks(callId: String?) {
        contentState.update { state ->
            state.copy(
                mocksDisplayed = MockDisplayed(
                    fromNetworkCallId = callId,
                ),
            )
        }
    }

    private fun closeMocks() {
        contentState.update { state ->
            state.copy(
                mocksDisplayed = null,
            )
        }
    }

    private fun openBadNetworkQuality() {
        contentState.update { state ->
            state.copy(
                badNetworkQualityDisplayed = true,
            )
        }
    }

    private fun closeBadNetworkQuality() {
        contentState.update { state ->
            state.copy(
                badNetworkQualityDisplayed = false,
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
                detailJsons = state.detailJsons + NetworkBodyDetailUi(
                    id = action.id,
                    text = action.json,
                ),
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
            val domainModel = observeNetworkRequestsByIdUseCase(action.item.uuid).firstOrNull()
                ?: return@launch
            val curl = generateCurlCommandUseCase(domainModel)
            copyToClipboard(curl)
        }
    }

    private fun onCopyUrl(action: NetworkAction.CopyUrl) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val domainModel = observeNetworkRequestsByIdUseCase(action.item.uuid).firstOrNull()
                ?: return@launch
            copyToClipboard(domainModel.request.url)
        }
    }

    private fun onRemove(action: NetworkAction.Remove) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            removeNetworkRequestUseCase(requestId = action.item.uuid)
        }
    }

    private fun onRemoveLinesAbove(action: NetworkAction.RemoveLinesAbove) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            removeHttpRequestsBeforeUseCase(requestId = action.item.uuid)
        }
    }

    private fun onFilterQuery(action: NetworkAction.FilterQuery) {
        _filterText.value = action.query
    }

    private fun onExportCsv() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            filteredItems.firstOrNull()?.let {
                val ids = it.map { it.uuid }
                exportNetworkCallsToCsv(ids).fold(
                    doOnFailure = {
                        feedbackDisplayer.displayMessage(
                            "Error while exporting csv"
                        )
                    },
                    doOnSuccess = { path ->
                        feedbackDisplayer.displayMessage(
                            "Csv exported at $path"
                        )
                    }
                )
            }
        }
    }
}

private fun Map<NetworkTextFilterColumns, TextFilterStateUiModel>.toDomain(): List<Filters>? {
    return buildList {
        this@toDomain.forEach { (column, filter) ->
            if (filter.isEnabled) {
                val includedFilters = filter.includedFilters.mapNotNull {
                    it.toDomain()
                }
                val excludedFilters = filter.excludedFilters.mapNotNull {
                    it.toDomain()
                }
                if (includedFilters.isNotEmpty() || excludedFilters.isNotEmpty()) {
                    add(
                        Filters(
                            column = column,
                            includedFilters = includedFilters,
                            excludedFilters = excludedFilters,
                        )
                    )
                }
            }
        }
    }
}

private fun TextFilterStateUiModel.FilterItem.toDomain(): NetworkFilterDomainModel.Filters.FilterItem? {
    return if(isActive) {
        NetworkFilterDomainModel.Filters.FilterItem(
            text = text,
        )
    } else null
}
