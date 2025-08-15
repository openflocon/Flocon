package io.github.openflocon.flocondesktop.features.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.network.usecase.GenerateCurlCommandUseCase
import io.github.openflocon.domain.network.usecase.ObserveHttpRequestsByIdUseCase
import io.github.openflocon.domain.network.usecase.ObserveHttpRequestsUseCase
import io.github.openflocon.domain.network.usecase.RemoveHttpRequestUseCase
import io.github.openflocon.domain.network.usecase.RemoveHttpRequestsBeforeUseCase
import io.github.openflocon.domain.network.usecase.ResetCurrentDeviceHttpRequestsUseCase
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.network.delegate.HeaderDelegate
import io.github.openflocon.flocondesktop.features.network.mapper.toDetailUi
import io.github.openflocon.flocondesktop.features.network.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.model.NetworkBodyDetailUi
import io.github.openflocon.flocondesktop.features.network.model.NetworkDetailViewState
import io.github.openflocon.library.designsystem.common.copyToClipboard
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
import kotlin.collections.plus

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
    private val sortAndFilterNetworkItemsProcessor: SortAndFilterNetworkItemsProcessor,
) : ViewModel(headerDelegate) {

    private val contentState = MutableStateFlow(
        ContentUiState(
            selectedRequestId = null,
            detailJsons = emptySet(),
            mocksDisplayed = null,
        ),
    )

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
        observeHttpRequestsUseCase(lite = true).map { list ->
            list.map {
                Pair(
                    it,
                    toUi(it),
                )
            }
        }, // keep the domain for the filter
        filterUiState,
        headerDelegate.sorted,
        headerDelegate.allowedMethods(),
        headerDelegate.textFiltersState,
    ) { items, filterState, sorted, allowedMethods, textFilters ->
        sortAndFilterNetworkItemsProcessor(
            items = items,
            filterState = filterState,
            sorted = sorted,
            allowedMethods = allowedMethods,
            textFilters = textFilters,
        )
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
            is NetworkAction.CloseMocks -> closeMocks()
            is NetworkAction.CopyCUrl -> onCopyCUrl(action)
            is NetworkAction.CopyUrl -> onCopyUrl(action)
            is NetworkAction.Remove -> onRemove(action)
            is NetworkAction.RemoveLinesAbove -> onRemoveLinesAbove(action)
            is NetworkAction.FilterQuery -> onFilterQuery(action)
            is NetworkAction.CloseJsonDetail -> onCloseJsonDetail(action)
            is NetworkAction.JsonDetail -> onJsonDetail(action)
            is NetworkAction.HeaderAction.ClickOnSort -> headerDelegate.onClickSort(
                type = action.type,
                sort = action.sort,
            )

            is NetworkAction.HeaderAction.FilterAction -> headerDelegate.onFilterAction(
                action = action.action,
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
            copyToClipboard(domainModel.networkRequest.url)
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
