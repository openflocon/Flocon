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

    /**
     * TODO Merge it with List<NetworkItemViewState> to a UiState?
     */
    val filters = listOf(
        MethodFilter()
    )

    val test = observeHttpRequestsUseCase().flatMapLatest { list ->
        combine(filters.map { it.filter(list) }) { array ->
            val duplicateRequests = array.toList().flatten()

            list.filter { request -> duplicateRequests.count { it == request } == filters.size }
        }
    }

    val state: StateFlow<List<NetworkItemViewState>> =
        observeHttpRequestsUseCase().flatMapLatest { list ->
            combine(filters.map { it.filter(list) }) { array ->
                val duplicateRequests = array.toList().flatten()

                list.filter { request -> duplicateRequests.count { it == request } == filters.size }
            }
        }
            .map { list -> list.map { toUi(it) } }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), emptyList())

    private val clickedRequestId = MutableStateFlow<String?>(null)

    val detailState: StateFlow<NetworkDetailViewState?> =
        clickedRequestId
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

                is OnNetworkItemUserAction.OnClicked -> {
                    clickedRequestId.update {
                        if (it == action.item.uuid) {
                            null
                        } else {
                            action.item.uuid
                        }
                    }
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
            clickedRequestId.update { null }
        }
    }

    fun onReset() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            resetCurrentDeviceHttpRequestsUseCase()
        }
    }

    private fun provideFilters() = combine(
        filters.map { filter ->
            filter.filter
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = false
                )
                .map { filter to it }
        }
    ) { list ->
        list.filter(Pair<MethodFilter, Boolean>::second)
            .map(Pair<MethodFilter, Boolean>::first)
    }

}
