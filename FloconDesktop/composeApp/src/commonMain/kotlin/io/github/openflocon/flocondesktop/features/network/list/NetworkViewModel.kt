package io.github.openflocon.flocondesktop.features.network.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.network.usecase.DecodeJwtTokenUseCase
import io.github.openflocon.domain.network.usecase.ExportNetworkCallsToCsvUseCase
import io.github.openflocon.domain.network.usecase.GenerateCurlCommandUseCase
import io.github.openflocon.domain.network.usecase.ObserveHttpRequestsByIdUseCase
import io.github.openflocon.domain.network.usecase.ObserveHttpRequestsUseCase
import io.github.openflocon.domain.network.usecase.RemoveHttpRequestUseCase
import io.github.openflocon.domain.network.usecase.RemoveHttpRequestsBeforeUseCase
import io.github.openflocon.domain.network.usecase.ResetCurrentDeviceHttpRequestsUseCase
import io.github.openflocon.domain.network.usecase.badquality.ObserveAllNetworkBadQualitiesUseCase
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkMocksUseCase
import io.github.openflocon.flocondesktop.features.network.body.model.ContentUiState
import io.github.openflocon.flocondesktop.features.network.body.model.MockDisplayed
import io.github.openflocon.flocondesktop.features.network.detail.mapper.toDetailUi
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.list.delegate.HeaderDelegate
import io.github.openflocon.flocondesktop.features.network.list.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.list.model.FilterUiState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkUiState
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.TextFilterStateUiModel
import io.github.openflocon.flocondesktop.features.network.list.processor.SortAndFilterNetworkItemsProcessor
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NetworkViewModel(
    observeHttpRequestsUseCase: ObserveHttpRequestsUseCase,
    private val observeHttpRequestsByIdUseCase: ObserveHttpRequestsByIdUseCase,
    private val generateCurlCommandUseCase: GenerateCurlCommandUseCase,
    private val resetCurrentDeviceHttpRequestsUseCase: ResetCurrentDeviceHttpRequestsUseCase,
    private val removeHttpRequestsBeforeUseCase: RemoveHttpRequestsBeforeUseCase,
    private val removeHttpRequestUseCase: RemoveHttpRequestUseCase,
    private val mocksUseCase: ObserveNetworkMocksUseCase,
    private val badNetworkUseCase: ObserveAllNetworkBadQualitiesUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val headerDelegate: HeaderDelegate,
    private val sortAndFilterNetworkItemsProcessor: SortAndFilterNetworkItemsProcessor,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val exportNetworkCallsToCsv: ExportNetworkCallsToCsvUseCase,
    private val decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
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

    private val _filterUiState = MutableStateFlow(FilterUiState(query = "", hasBadNetwork = false, hasMocks = false))
    private val filterUiState = combine(
        _filterUiState,
        mocksUseCase().map { it.any(MockNetworkDomainModel::isEnabled) },
        badNetworkUseCase().map { it.any(BadQualityConfigDomainModel::isEnabled) }
    ) { state, mockEnabled, badNetworkEnabled ->
        state.copy(hasMocks = mockEnabled, hasBadNetwork = badNetworkEnabled)
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), _filterUiState.value)

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

    private val items = combine(
        observeHttpRequestsUseCase(),
        observeCurrentDeviceIdAndPackageNameUseCase(),
    ) { list, deviceIdAndPackageName ->
        list.map { networkCall ->
            networkCall to toUi(
                networkCall = networkCall,
                deviceIdAndPackageName = deviceIdAndPackageName
            )
        } // keep the domain for the filter
    }

    data class FilterConfig(
        val filterState: FilterUiState,
        val sorted: HeaderDelegate.Sorted?,
        val allowedMethods: List<NetworkMethodUi>,
        val textFilters: Map<NetworkTextFilterColumns, TextFilterStateUiModel>,
    )

    private val filterConfig = combine(
        filterUiState,
        headerDelegate.sorted,
        headerDelegate.allowedMethods(),
        headerDelegate.textFiltersState,
    ) { filterState, sorted, allowedMethods, textFilters ->
        FilterConfig(
            filterState = filterState,
            sorted = sorted,
            allowedMethods = allowedMethods,
            textFilters = textFilters,
        )
    }
        .distinctUntilChanged()

    private val filteredItems: Flow<List<NetworkItemViewState>> = combine(
        items,
        contentState,
        filterConfig
    ) { items, content, config ->
        sortAndFilterNetworkItemsProcessor(
            items = items,
            filterState = config.filterState,
            sorted = config.sorted,
            allowedMethods = config.allowedMethods,
            textFilters = config.textFilters
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
            NetworkAction.AutoScroll -> onAutoScroll()
            NetworkAction.ClearSession -> onClearSession()
        }
    }

    private fun onClearSession() {

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
            copyToClipboard(domainModel.request.url)
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
        _filterUiState.update { state ->
            state.copy(query = action.query)
        }
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
