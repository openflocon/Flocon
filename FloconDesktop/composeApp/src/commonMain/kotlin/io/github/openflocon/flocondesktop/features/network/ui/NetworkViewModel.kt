package io.github.openflocon.flocondesktop.features.network.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.copyToClipboard
import io.github.openflocon.flocondesktop.features.network.domain.GenerateCurlCommandUseCase
import io.github.openflocon.flocondesktop.features.network.domain.ObserveHttpRequestsByIdUseCase
import io.github.openflocon.flocondesktop.features.network.domain.ObserveHttpRequestsUseCase
import io.github.openflocon.flocondesktop.features.network.domain.RemoveHttpRequestUseCase
import io.github.openflocon.flocondesktop.features.network.domain.RemoveHttpRequestsBeforeUseCase
import io.github.openflocon.flocondesktop.features.network.domain.ResetCurrentDeviceHttpRequestsUseCase
import io.github.openflocon.flocondesktop.features.network.ui.mapper.toDetailUi
import io.github.openflocon.flocondesktop.features.network.ui.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.OnNetworkItemUserAction
import io.github.openflocon.flocondesktop.features.network.ui.view.filters.Filters
import io.github.openflocon.flocondesktop.features.network.ui.view.filters.MethodFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
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
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    private val contentUiState = MutableStateFlow(ContentUiState())
    private val filterUiState = MutableStateFlow(FilterUiState())

    private val detailState: StateFlow<NetworkDetailViewState?> =
        contentUiState.map { it.selectedRequestId }
            .flatMapLatest { id ->
                if (id == null) {
                    flowOf(null)
                } else {
                    observeHttpRequestsByIdUseCase(id)
                        .distinctUntilChanged()
                        .map {
                            it?.let {
                                toDetailUi(it)
                            }
                        }
                }
            }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), null)

    val uiState = combine(
        filterUiState.asStateFlow(),
        detailState
    ) { filterState, detailState ->
        NetworkUiState(
            detailState = detailState,
            filterState = filterState
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NetworkUiState()
        )

    /**
     * TODO Merge it with List<NetworkItemViewState> to a UiState?
     */
    private val _filters = MutableStateFlow<List<Filters>>(listOf(MethodFilter()))
    val filters = _filters.asStateFlow()

    /**
     * TODO Change to Paging
     */
    val state: StateFlow<List<NetworkItemViewState>> = combineTransform(
        observeHttpRequestsUseCase(),
        filters
    ) { requests, filters ->
        emit(requests)
        // TODO Rework
//        emitAll(
//            combine(filters.map { it.filter(requests) }) { array ->
//                val duplicateRequests = array.toList().flatten()
//
//                requests.filter { request -> duplicateRequests.count { it == request } == filters.size }  // TODO Not sure about this, doesn't seems efficient
//            }
//        )
    }
        .map { list -> list.map { toUi(it) } }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onAction(action: NetworkAction) {
        when (action) {
            is NetworkAction.SelectRequest -> onSelectRequest(action)
        }
    }

    private fun onSelectRequest(action: NetworkAction.SelectRequest) {
        contentUiState.update { state ->
            state.copy(
                selectedRequestId = if (state.selectedRequestId == action.id) {
                    null
                } else {
                    action.id
                }
            )
        }
    }

    fun onNetworkItemUserAction(action: OnNetworkItemUserAction) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (action) {
                is OnNetworkItemUserAction.CopyCUrl -> {
                    val domainModel = observeHttpRequestsByIdUseCase(action.item.uuid).firstOrNull()
                        ?: return@launch
                    val curl = generateCurlCommandUseCase(domainModel)
                    copyToClipboard(curl)
                }

                is OnNetworkItemUserAction.CopyUrl -> {
                    val domainModel = observeHttpRequestsByIdUseCase(action.item.uuid).firstOrNull()
                        ?: return@launch
                    copyToClipboard(domainModel.url)
                }

                is OnNetworkItemUserAction.Remove -> {
                    removeHttpRequestUseCase(requestId = action.item.uuid)
                }

                is OnNetworkItemUserAction.RemoveLinesAbove -> {
                    removeHttpRequestsBeforeUseCase(requestId = action.item.uuid)
                }
            }
        }
    }

    fun onCopyText(text: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            copyToClipboard(text)
            feedbackDisplayer.displayMessage("copied")
        }
    }

    fun closeDetailPanel() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            contentUiState.update { it.copy(selectedRequestId = null) }
        }
    }

    fun onReset() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            resetCurrentDeviceHttpRequestsUseCase()
        }
    }

}
