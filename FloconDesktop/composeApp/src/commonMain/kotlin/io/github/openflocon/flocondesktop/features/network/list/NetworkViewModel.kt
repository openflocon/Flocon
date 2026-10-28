package io.github.openflocon.flocondesktop.features.network.list

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel
import io.github.openflocon.domain.network.models.NetworkSortDomainModel
import io.github.openflocon.domain.network.usecase.ExportNetworkCallsToCsvUseCase
import io.github.openflocon.domain.network.usecase.GetNetworkRequestsUseCase
import io.github.openflocon.domain.network.usecase.ImportNetworkCallsFromCsvUseCase
import io.github.openflocon.domain.network.usecase.ObserveCurrentDeviceHasNetworkRequestsUseCase
import io.github.openflocon.domain.network.usecase.ObserveNetworkRequestsUseCase
import io.github.openflocon.domain.network.usecase.RemoveHttpRequestsBeforeUseCase
import io.github.openflocon.domain.network.usecase.RemoveNetworkRequestUseCase
import io.github.openflocon.domain.network.usecase.RemoveOldSessionsNetworkRequestUseCase
import io.github.openflocon.domain.network.usecase.ResetCurrentDeviceHttpRequestsUseCase
import io.github.openflocon.domain.network.usecase.badquality.ObserveAllNetworkBadQualitiesUseCase
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkWebsocketIdsUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.core.data.settings.usecase.ObserveNetworkSettingsUseCase
import io.github.openflocon.flocondesktop.core.data.settings.usecase.SaveNetworkSettingsUseCase
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.flocondesktop.features.network.body.model.ContentUiState
import io.github.openflocon.flocondesktop.features.network.list.delegate.HeaderDelegate
import io.github.openflocon.flocondesktop.features.network.list.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.list.model.FilterUiState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkSettingsUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkUiState
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOptionsDelegate
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class NetworkViewModel(
    observeNetworkRequestsUseCase: ObserveNetworkRequestsUseCase,
    observeCurrentDeviceHasNetworkRequestsUseCase: ObserveCurrentDeviceHasNetworkRequestsUseCase,
    private val removeHttpRequestsBeforeUseCase: RemoveHttpRequestsBeforeUseCase,
    private val removeNetworkRequestUseCase: RemoveNetworkRequestUseCase,
    private val mocksUseCase: ObserveNetworkMocksUseCase,
    private val badNetworkUseCase: ObserveAllNetworkBadQualitiesUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val headerDelegate: HeaderDelegate,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val navigationState: MainFloconNavigationState,
    private val observeNetworkSettingsUseCase: ObserveNetworkSettingsUseCase,
    private val observeNetworkWebsocketIdsUseCase: ObserveNetworkWebsocketIdsUseCase,
    private val networkItemOptionsDelegate: NetworkItemOptionsDelegate,
) : ViewModel(headerDelegate),
    KoinComponent {

    // lazy inject the actions we might don't need
    private val importNetworkCallsFromCsvUseCase: ImportNetworkCallsFromCsvUseCase by inject()
    private val saveNetworkSettingsUseCase: SaveNetworkSettingsUseCase by inject()
    private val removeOldSessionsNetworkRequestUseCase: RemoveOldSessionsNetworkRequestUseCase by inject()
    private val resetCurrentDeviceHttpRequestsUseCase: ResetCurrentDeviceHttpRequestsUseCase by inject()
    private val getNetworkRequestsUseCase: GetNetworkRequestsUseCase by inject()
    private val feedbackDisplayer: FeedbackDisplayer by inject()
    private val exportNetworkCallsToCsv: ExportNetworkCallsToCsvUseCase by inject()

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

    private val settings: StateFlow<NetworkSettingsUiModel> = observeNetworkSettingsUseCase()
        .map { it.toUi() }
        .stateInWhileSubscribed(defaultNetworkSettings.toUi())

    val isPinned = settings.map { it.pinPanel }
        .stateInWhileSubscribed(settings.value.pinPanel)

    private val mocks = mocksUseCase()
        .map { it.any(MockNetworkDomainModel::isEnabled) }
        .stateInWhileSubscribed(false)

    private val badNetwork = badNetworkUseCase()
        .map { it.any(BadQualityConfigDomainModel::isEnabled) }
        .stateInWhileSubscribed(false)

    private val hasWebsockets = observeNetworkWebsocketIdsUseCase()
        .map { it.isNotEmpty() }
        .stateInWhileSubscribed(false)

    // no call at all for the app, whatever the filters
    private val showEmptyState: StateFlow<Boolean> = observeCurrentDeviceHasNetworkRequestsUseCase()
        .map { hasRequests -> !hasRequests }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val uiState = viewModelScope.launchMolecule(RecompositionMode.Immediate) {
        val showEmptyState by showEmptyState.collectAsState()
        val settings by settings.collectAsState()
        val content by contentState.collectAsState()
        val header by headerDelegate.headerState.collectAsState()
        val mockEnabled by mocks.collectAsState()
        val badNetworkEnabled by badNetwork.collectAsState()
        val hasWebsockets by hasWebsockets.collectAsState()

        val filter = FilterUiState(
            hasBadNetwork = badNetworkEnabled,
            hasMocks = mockEnabled,
            displayOldSessions = settings.displayOldSessions,
            hasWebsockets = hasWebsockets
        )

        NetworkUiState(
            showEmptyState = showEmptyState,
            contentState = content,
            filterState = filter,
            headerState = header,
            settings = settings,
        )
    }

    // true while the reset is running: the paging list can take a few seconds to reflect
    // the delete, so we display an empty list immediately instead of waiting for Room
    private val isClearing = MutableStateFlow(false)

    private val deviceIdAndPackageName = observeCurrentDeviceIdAndPackageNameUseCase()
        .stateInWhileSubscribed(null)

    private val query = viewModelScope.launchMolecule(RecompositionMode.Immediate) {
        val device by deviceIdAndPackageName.collectAsState()
        val clearing by isClearing.collectAsState()
        val header by headerDelegate.queryInputs.collectAsState()
        val settings by settings.collectAsState()

        buildNetworkListQuery(
            deviceIdAndPackageName = device,
            isClearing = clearing,
            header = header,
            filterText = filterText.value,
            displayOldSessions = settings.displayOldSessions,
        )
    }

    val items: Flow<PagingData<NetworkItemViewState>> = query
        .flatMapLatest { query ->
            if (query.isClearing) return@flatMapLatest flowOf(PagingData.empty())
            observeNetworkRequestsUseCase(
                sortedBy = query.sort,
                filter = query.filter,
                deviceIdAndPackageName = query.deviceIdAndPackageName,
            ).map { networkCallPagingData ->
                networkCallPagingData.map {
                    it.toUi(deviceIdAndPackageName = query.deviceIdAndPackageName)
                }
            }
        }
        .flowOn(dispatcherProvider.viewModel)
        .cachedIn(viewModelScope)

    fun onAction(action: NetworkAction) {
        when (action) {
            is NetworkAction.SelectRequest -> onSelectRequest(action)

            is NetworkAction.ClosePanel -> onClosePanel()

            is NetworkAction.Reset -> onReset()

            is NetworkAction.OpenMocks -> openMocks(callId = null)

            is NetworkAction.OpenBadNetworkQuality -> openBadNetworkQuality()

            is NetworkAction.CloseBadNetworkQuality -> closeBadNetworkQuality()

            is NetworkAction.ItemOption -> onItemOption(action)

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

            is NetworkAction.SetDefaultSelectedTab -> setDefaultSelectedTab(action)

            is NetworkAction.SelectLine -> onSelectLine(action)

            NetworkAction.ClearMultiSelect -> onClearMultiSelect()

            NetworkAction.MultiSelect -> onMultiSelect()

            NetworkAction.DeleteSelection -> onDeleteSelection()

            is NetworkAction.DoubleClicked -> onDoubleClicked(action)

            NetworkAction.OpenDeepSearch -> navigationState.navigate(NetworkRoutes.DeepSearch)
        }
    }

    private fun onItemOption(action: NetworkAction.ItemOption) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            networkItemOptionsDelegate.onOption(callId = action.id, option = action.option)
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
            val settings = observeNetworkSettingsUseCase().firstOrNull() ?: return@launch

            saveNetworkSettingsUseCase(
                settings.copy(
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
            val settings = observeNetworkSettingsUseCase().firstOrNull() ?: return@launch

            saveNetworkSettingsUseCase(
                settings.copy(
                    autoScroll = action.value
                )
            )
        }
    }

    private fun toggleDisplayOldSessions(action: NetworkAction.UpdateDisplayOldSessions) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val settings = observeNetworkSettingsUseCase().firstOrNull() ?: return@launch

            saveNetworkSettingsUseCase(
                settings.copy(
                    displayOldSessions = action.value
                )
            )
        }
    }

    private fun toggleInvertList(action: NetworkAction.InvertList) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val settings = observeNetworkSettingsUseCase().firstOrNull() ?: return@launch

            saveNetworkSettingsUseCase(
                settings.copy(
                    invertList = action.value
                )
            )
        }
    }

    private fun setDefaultSelectedTab(action: NetworkAction.SetDefaultSelectedTab) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val settings = observeNetworkSettingsUseCase().firstOrNull() ?: return@launch

            saveNetworkSettingsUseCase(
                settings.copy(
                    defaultSelectedTab = action.tab
                )
            )
        }
    }

    private fun onSelectRequest(action: NetworkAction.SelectRequest) {
        selectRequest(action.id)
    }

    private fun selectRequest(id: String) {
        contentState.update { it.copy(selectedRequestId = id) }
        navigationState.navigate(NetworkRoutes.Panel(id))
    }

    private fun openMocks(callId: String?) {
        navigationState.navigate(NetworkRoutes.Mocks(callId))
    }

    private fun openWebsocketMocks() {
        contentState.update { state ->
            state.copy(websocketMocksDisplayed = true)
        }
    }

    private fun openBadNetworkQuality() {
        contentState.update { state ->
            state.copy(badNetworkQualityDisplayed = true)
        }
    }

    private fun closeBadNetworkQuality() {
        contentState.update { state ->
            state.copy(badNetworkQualityDisplayed = false)
        }
    }

    private fun onClosePanel() {
        contentState.update { it.copy(selectedRequestId = null) }
        navigationState.closePanel()
    }

    private fun onReset() {
        isClearing.value = true
        contentState.update {
            it.copy(
                selectedRequestId = null,
                selecting = false,
                multiSelectedIds = emptySet(),
            )
        }
        viewModelScope.launch(dispatcherProvider.viewModel) {
            try {
                resetCurrentDeviceHttpRequestsUseCase()
            } finally {
                isClearing.value = false
            }
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
            val query = query.value
            val requestIds = getNetworkRequestsUseCase(
                sortedBy = query.sort,
                filter = query.filter,
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

    @OptIn(ExperimentalUuidApi::class)
    private fun onDoubleClicked(action: NetworkAction.DoubleClicked) {
        navigationState.navigate(
            NetworkRoutes.WindowDetail(
                requestId = action.item.uuid,
                windowKey = Uuid.random().toString(),
            )
        )
    }

    companion object {
        private val defaultNetworkSettings = NetworkSettings(
            displayOldSessions = true,
            autoScroll = false,
            invertList = false,
            pinnedDetails = false
        )
    }
}

internal data class NetworkListQuery(
    val deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel?,
    val isClearing: Boolean,
    val sort: NetworkSortDomainModel?,
    val filter: NetworkFilterDomainModel,
)

internal fun buildNetworkListQuery(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel?,
    isClearing: Boolean,
    header: HeaderDelegate.HeaderQuery,
    filterText: String,
    displayOldSessions: Boolean,
) = NetworkListQuery(
    deviceIdAndPackageName = deviceIdAndPackageName,
    isClearing = isClearing,
    sort = header.sort,
    filter = NetworkFilterDomainModel(
        filterOnAllColumns = filterText.takeIf(String::isNotBlank),
        textsFilters = header.textFilters,
        methodFilter = header.methods,
        displayOldSessions = displayOldSessions,
    ),
)
