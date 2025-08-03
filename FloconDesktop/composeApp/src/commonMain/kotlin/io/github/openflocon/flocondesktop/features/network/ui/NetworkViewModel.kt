package io.github.openflocon.flocondesktop.features.network.ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
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
import io.github.openflocon.flocondesktop.features.network.ui.view.filters.Filters
import io.github.openflocon.flocondesktop.features.network.ui.view.filters.MethodFilter
import io.github.openflocon.flocondesktop.features.network.ui.view.filters.MethodFilter.Methods
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                        .map { it?.let { toDetailUi(it) } }
                }
            }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), null)

    val uiState = viewModelScope.launchMolecule(RecompositionMode.Immediate) {
        val items by observeHttpRequestsUseCase().collectAsState(emptyList())
        val filterState by filterUiState.collectAsState()
        val detailState by detailState.collectAsState()

        var filteredItems by remember { mutableStateOf(emptyList<NetworkItemViewState>()) }

        LaunchedEffect(items, filterState) {
            filteredItems = filterItems(items, filterState).map { toUi(it) }
        }

        NetworkUiState(
            items = filteredItems,
            detailState = detailState,
            filterState = filterState
        )
    }

    /**
     * TODO Merge it with List<NetworkItemViewState> to a UiState?
     */
    private val _filters = MutableStateFlow<List<Filters>>(listOf(MethodFilter()))
    val filters = _filters.asStateFlow()

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

    private fun onClosePanel() {
        contentUiState.update { it.copy(selectedRequestId = null) }
    }

    private fun onCopyText(action: NetworkAction.CopyText) {
        copyToClipboard(action.text)
        feedbackDisplayer.displayMessage("copied")
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

    private fun filterItems(
        items: List<FloconHttpRequestDomainModel>,
        filterState: FilterUiState
    ): List<FloconHttpRequestDomainModel> {
        var filteredItems = items

        if (filterState.methods.isNotEmpty())
            filteredItems = filteredItems.filter { item ->
                when (item.type) {
                    is FloconHttpRequestDomainModel.Type.GraphQl -> filterState.methods.contains(
                        Methods.GraphQL
                    )

                    is FloconHttpRequestDomainModel.Type.Grpc -> filterState.methods.contains(
                        Methods.Grpc
                    )

                    is FloconHttpRequestDomainModel.Type.Http -> filterState.methods.filterIsInstance<Methods.Http>()
                        .map(Methods.Http::methodName)
                        .contains(item.request.method)
                }
            }

        return filteredItems
    }

}
