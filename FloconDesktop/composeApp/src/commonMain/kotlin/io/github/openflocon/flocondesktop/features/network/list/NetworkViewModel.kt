package io.github.openflocon.flocondesktop.features.network.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel.Filters
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.network.usecase.ExportNetworkCallsToCsvUseCase
import io.github.openflocon.domain.network.usecase.GenerateCurlCommandUseCase
import io.github.openflocon.domain.network.usecase.GetNetworkRequestsUseCase
import io.github.openflocon.domain.network.usecase.ImportNetworkCallsFromCsvUseCase
import io.github.openflocon.domain.network.usecase.ObserveNetworkRequestsByIdUseCase
import io.github.openflocon.domain.network.usecase.ObserveNetworkRequestsUseCase
import io.github.openflocon.domain.network.usecase.RemoveHttpRequestsBeforeUseCase
import io.github.openflocon.domain.network.usecase.RemoveNetworkRequestUseCase
import io.github.openflocon.domain.network.usecase.RemoveOldSessionsNetworkRequestUseCase
import io.github.openflocon.domain.network.usecase.ReplayNetworkCallUseCase
import io.github.openflocon.domain.network.usecase.ResetCurrentDeviceHttpRequestsUseCase
import io.github.openflocon.domain.network.usecase.badquality.ObserveAllNetworkBadQualitiesUseCase
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkWebsocketIdsUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.core.data.settings.usecase.ObserveNetworkSettingsUseCase
import io.github.openflocon.flocondesktop.core.data.settings.usecase.SaveNetworkSettingsUseCase
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.flocondesktop.features.network.body.model.ContentUiState
import io.github.openflocon.flocondesktop.features.network.detail.NetworkDetailDelegate
import io.github.openflocon.flocondesktop.features.network.list.delegate.HeaderDelegate
import io.github.openflocon.flocondesktop.features.network.list.mapper.toDomain
import io.github.openflocon.flocondesktop.features.network.list.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkUiState
import io.github.openflocon.flocondesktop.features.network.list.model.TopBarUiState
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.TextFilterStateUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NetworkViewModel(
    observeNetworkRequestsUseCase: ObserveNetworkRequestsUseCase,
    private val observeNetworkRequestsByIdUseCase: ObserveNetworkRequestsByIdUseCase,
    private val removeHttpRequestsBeforeUseCase: RemoveHttpRequestsBeforeUseCase,
    private val removeNetworkRequestUseCase: RemoveNetworkRequestUseCase,
    private val mocksUseCase: ObserveNetworkMocksUseCase,
    private val badNetworkUseCase: ObserveAllNetworkBadQualitiesUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val headerDelegate: HeaderDelegate,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val navigationState: MainFloconNavigationState,
    private val detailDelegate: NetworkDetailDelegate,
    private val observeNetworkSettingsUseCase: ObserveNetworkSettingsUseCase,
    private val observeNetworkWebsocketIdsUseCase: ObserveNetworkWebsocketIdsUseCase,
) : ViewModel(headerDelegate),
    KoinComponent {

    // lazy inject the actions we might don't need
    private val importNetworkCallsFromCsvUseCase: ImportNetworkCallsFromCsvUseCase by inject()
    private val saveNetworkSettingsUseCase: SaveNetworkSettingsUseCase by inject()
    private val removeOldSessionsNetworkRequestUseCase: RemoveOldSessionsNetworkRequestUseCase by inject()
    private val generateCurlCommandUseCase: GenerateCurlCommandUseCase by inject()
    private val resetCurrentDeviceHttpRequestsUseCase: ResetCurrentDeviceHttpRequestsUseCase by inject()
    private val getNetworkRequestsUseCase: GetNetworkRequestsUseCase by inject()
    private val feedbackDisplayer: FeedbackDisplayer by inject()
    private val exportNetworkCallsToCsv: ExportNetworkCallsToCsvUseCase by inject()
    private val replayNetworkCallUseCase: ReplayNetworkCallUseCase by inject()

    private val contentState = MutableStateFlow(
        ContentUiState(
            selectedRequestId = null,
            badNetworkQualityDisplayed = false,
            websocketMocksDisplayed = false,
            selecting = false,
            multiSelectedIds = emptySet()
        )
    )

    private val _filterText = mutableStateOf("")
    val filterText: State<String> = _filterText

    private val defaultNetworkSettings = NetworkSettings(
        displayOldSessions = true,
        autoScroll = false,
        invertList = false,
        pinnedDetails = false
    )

    private val settings: StateFlow<NetworkSettings> = observeNetworkSettingsUseCase()
        .flowOn(dispatcherProvider.viewModel)
        .stateInWhileSubscribed(defaultNetworkSettings)

    private val filterUiState = combine(
        mocksUseCase().map { it.any(MockNetworkDomainModel::isEnabled) }.distinctUntilChanged(),
        badNetworkUseCase().map { it.any(BadQualityConfigDomainModel::isEnabled) }
            .distinctUntilChanged(),
        settings,
        observeNetworkWebsocketIdsUseCase().map { it.isNotEmpty() },
    ) { mockEnabled, badNetworkEnabled, settings, hasWebsockets ->
        TopBarUiState(
            hasBadNetwork = badNetworkEnabled,
            hasMocks = mockEnabled,
            displayOldSessions = settings.displayOldSessions,
            hasWebsockets = hasWebsockets,
        )
    }
        .stateInWhileSubscribed(
            TopBarUiState(
                hasBadNetwork = false,
                hasMocks = false,
                displayOldSessions = false,
                hasWebsockets = false,
            )
        )

    private val filter = combines(
        snapshotFlow { _filterText.value }.map { it.takeIf { it.isNotBlank() } }
            .distinctUntilChanged(),
        headerDelegate.textFiltersState.map { it.toDomain() }.distinctUntilChanged(),
        headerDelegate.allowedMethods().map { items -> methodsToDomain(items) }
            .distinctUntilChanged(),
        settings,
    )
        .map { (textFilters, filterOnAllColumns, methods, settings) ->
            NetworkFilterDomainModel(
                filterOnAllColumns = textFilters,
                textsFilters = filterOnAllColumns,
                methodFilter = methods,
                displayOldSessions = settings.displayOldSessions,
            )
        }

    private val sortAndFilter = combines(
        headerDelegate.sorted.map { it?.toDomain() }.distinctUntilChanged(),
        filter,
    )
        .distinctUntilChanged()

    val items: Flow<PagingData<NetworkItemViewState>> =
        observeCurrentDeviceIdAndPackageNameUseCase()
            .flatMapLatest { deviceIdAndPackageName ->
                sortAndFilter.flatMapLatest { (sorted, filter) ->
                    observeNetworkRequestsUseCase(
                        sortedBy = sorted,
                        filter = filter,
                        deviceIdAndPackageName = deviceIdAndPackageName,
                    ).map { networkCallPagingData ->
                        networkCallPagingData.map {
                            it.toUi(
                                deviceIdAndPackageName = deviceIdAndPackageName
                            )
                        }
                    }
                }
            }
            .flowOn(dispatcherProvider.viewModel)
            .cachedIn(viewModelScope)

    private val detailState = combine(
        detailDelegate.uiState,
        contentState,
        settings
    ) { state, content, settings ->
        state.takeIf { settings.pinnedDetails && content.selectedRequestId != null }
    }
        .stateInWhileSubscribed(null)

    val uiState = combine(
        contentState,
        detailState,
        filterUiState,
        headerDelegate.headerUiState,
        settings.map { it.toUi() },
    ) { content, detail, filter, header, settings ->
        NetworkUiState(
            contentState = content,
            detailState = detail,
            filterState = filter,
            headerState = header,
            settings = settings,
        )
    }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NetworkUiState(
                detailState = detailState.value,
                contentState = contentState.value,
                filterState = filterUiState.value,
                headerState = headerDelegate.headerUiState.value,
                settings = settings.value.toUi(),
            ),
        )

    fun onAction(action: NetworkAction) {
        when (action) {
            is NetworkAction.SelectRequest -> onSelectRequest(action)
            is NetworkAction.ClosePanel -> onClosePanel()
            is NetworkAction.Reset -> onReset()
            is NetworkAction.OpenMocks -> openMocks(callId = null)
            is NetworkAction.CreateMock -> {
                openMocks(callId = action.item.uuid)
            }

            is NetworkAction.OpenBadNetworkQuality -> openBadNetworkQuality()
            is NetworkAction.CloseBadNetworkQuality -> closeBadNetworkQuality()
            is NetworkAction.CopyCUrl -> onCopyCUrl(action)
            is NetworkAction.Replay -> onReplay(action)
            is NetworkAction.CopyUrl -> onCopyUrl(action)
            is NetworkAction.Remove -> onRemove(action)
            is NetworkAction.RemoveLinesAbove -> onRemoveLinesAbove(action)
            is NetworkAction.FilterQuery -> onFilterQuery(action)
            is NetworkAction.ExportCsv -> onExportCsv()
            is NetworkAction.ImportFromCsv -> onImportFromCsv()
            is NetworkAction.HeaderAction.ClickOnSort -> headerDelegate.onClickSort(
                type = action.type,
                sort = action.sort,
            )

            is NetworkAction.HeaderAction.FilterAction -> headerDelegate.onFilterAction(
                action = action.action,
            )

            is NetworkAction.InvertList -> toggleInvertList(action)
            is NetworkAction.ToggleAutoScroll -> toggleAutoScroll(action)
            NetworkAction.ClearOldSession -> onClearSession()
            is NetworkAction.Down -> selectRequest(action.itemIdToSelect)
            is NetworkAction.Up -> selectRequest(action.itemIdToSelect)
            is NetworkAction.UpdateDisplayOldSessions -> toggleDisplayOldSessions(action)
            NetworkAction.OpenWebsocketMocks -> openWebsocketMocks()
            NetworkAction.CloseWebsocketMocks -> contentState.update {
                it.copy(
                    websocketMocksDisplayed = false
                )
            }

            is NetworkAction.Pinned -> onPinned(action)
            is NetworkAction.DetailAction -> detailDelegate.onAction(action.action)
            is NetworkAction.SelectLine -> onSelectLine(action)
            NetworkAction.ClearMultiSelect -> onClearMultiSelect()
            NetworkAction.MultiSelect -> onMultiSelect()
            NetworkAction.DeleteSelection -> onDeleteSelection()
            is NetworkAction.DoubleClicked -> onDoubleClicked(action)
        }
    }

    private fun onDeleteSelection() {
        viewModelScope.launch {
            contentState.value
                .multiSelectedIds
                .map { async { removeNetworkRequestUseCase(it) } }
                .awaitAll()
        }
    }

    private fun onMultiSelect() {
        contentState.update { it.copy(selecting = true) }
    }

    private fun onClearMultiSelect() {
        contentState.update { state ->
            state.copy(
                selecting = false,
                multiSelectedIds = emptySet()
            )
        }
    }

    private fun onSelectLine(action: NetworkAction.SelectLine) {
        contentState.update {
            it.copy(
                selecting = true,
                multiSelectedIds = if (action.selected) {
                    it.multiSelectedIds.plus(action.id)
                } else {
                    it.multiSelectedIds.minus(action.id)
                }
            )
        }
    }

    private fun onPinned(action: NetworkAction.Pinned) {
        viewModelScope.launch {
            saveNetworkSettingsUseCase(
                settings.value.copy(
                    pinnedDetails = action.value
                )
            )
        }
    }

    private fun onClearSession() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            removeOldSessionsNetworkRequestUseCase()
        }
    }

    private fun toggleAutoScroll(action: NetworkAction.ToggleAutoScroll) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            saveNetworkSettingsUseCase(
                settings.value.copy(
                    autoScroll = action.value
                )
            )
        }
    }

    private fun toggleDisplayOldSessions(action: NetworkAction.UpdateDisplayOldSessions) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            saveNetworkSettingsUseCase(
                settings.value.copy(
                    displayOldSessions = action.value
                )
            )
        }
    }

    private fun toggleInvertList(action: NetworkAction.InvertList) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            saveNetworkSettingsUseCase(
                settings.value.copy(
                    invertList = action.value
                )
            )
        }
    }

    private fun onSelectRequest(action: NetworkAction.SelectRequest) {
        selectRequest(action.id)
    }

    private var selectRequestJob: Job? = null
    private fun selectRequest(id: String) {
        contentState.update { it.copy(selectedRequestId = id) }
        selectRequestJob?.cancel()
        selectRequestJob = viewModelScope.launch {
            observeNetworkSettingsUseCase().collect {
                if (it.pinnedDetails) {
                    detailDelegate.setRequestId(id)
                } else {
                    navigationState.navigate(NetworkRoutes.Panel(id))
                }
            }
        }
    }

    private fun openMocks(callId: String?) {
        navigationState.navigate(NetworkRoutes.Mocks(callId))
    }

    private fun openWebsocketMocks() {
        contentState.update { state ->
            state.copy(
                websocketMocksDisplayed = true,
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

    private fun onReplay(action: NetworkAction.Replay) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            replayNetworkCallUseCase(action.item.uuid)
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

    private fun onImportFromCsv() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            importNetworkCallsFromCsvUseCase().fold(
                doOnFailure = {
                    feedbackDisplayer.displayMessage(
                        "Error while importing csv : ${it.message}"
                    )
                },
                doOnSuccess = { path ->
                    feedbackDisplayer.displayMessage(
                        "Csv imported"
                    )
                }
            )
        }
    }

    private fun onExportCsv() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val sortAndFilter = sortAndFilter.firstOrNull() ?: return@launch
            val requestIds = getNetworkRequestsUseCase(
                sortedBy = sortAndFilter.first,
                filter = sortAndFilter.second,
            )
                .map(FloconNetworkCallDomainModel::callId)
            val ids = if (uiState.value.contentState.selecting) {
                requestIds.filter { it in uiState.value.contentState.multiSelectedIds }
            } else {
                requestIds
            }

            exportNetworkCallsToCsv(ids)
                .fold(
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
                .also { onClearMultiSelect() }
        }
    }
    private fun onDoubleClicked(action: NetworkAction.DoubleClicked) {
        navigationState.navigate(NetworkRoutes.WindowDetail(action.item.uuid))
    }


}

private fun Map<NetworkTextFilterColumns, TextFilterStateUiModel>.toDomain(): List<Filters> = buildList {
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

private fun TextFilterStateUiModel.FilterItem.toDomain(): Filters.FilterItem? = if (isActive) {
    Filters.FilterItem(
        text = text,
    )
} else null

private fun methodsToDomain(items: List<NetworkMethodUi>): List<String>? {
    return items.map { it.text }.takeIf { it.isNotEmpty() }
        ?.takeIf { it.size != NetworkMethodUi.all().size } // returns null if we accept all
}
