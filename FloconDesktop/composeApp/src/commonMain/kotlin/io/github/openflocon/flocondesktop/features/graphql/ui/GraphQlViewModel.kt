package io.github.openflocon.flocondesktop.features.graphql.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.copyToClipboard
import io.github.openflocon.flocondesktop.features.graphql.domain.DeleteGraphQlRequestUseCase
import io.github.openflocon.flocondesktop.features.graphql.domain.DeleteGraphQlRequestsBeforeUseCase
import io.github.openflocon.flocondesktop.features.graphql.domain.ObserveGraphQlRequestsByIdUseCase
import io.github.openflocon.flocondesktop.features.graphql.domain.ObserveGraphQlRequestsUseCase
import io.github.openflocon.flocondesktop.features.graphql.domain.ResetCurrentDeviceGraphQlRequestsUseCase
import io.github.openflocon.flocondesktop.features.graphql.ui.mapper.toDetailUi
import io.github.openflocon.flocondesktop.features.graphql.ui.mapper.toUi
import io.github.openflocon.flocondesktop.features.graphql.ui.model.GraphQlDetailViewState
import io.github.openflocon.flocondesktop.features.graphql.ui.model.GraphQlItemViewState
import io.github.openflocon.flocondesktop.features.graphql.ui.model.OnGraphQlItemUserAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GraphQlViewModel(
    observeGraphQlRequestsUseCase: ObserveGraphQlRequestsUseCase,
    private val observeGraphQlRequestsByIdUseCase: ObserveGraphQlRequestsByIdUseCase,
    private val resetCurrentDeviceGraphQlRequestsUseCase: ResetCurrentDeviceGraphQlRequestsUseCase,
    private val removeGraphQlRequestsBeforeUseCase: DeleteGraphQlRequestsBeforeUseCase,
    private val removeGraphQlRequestUseCase: DeleteGraphQlRequestUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    val state: StateFlow<List<GraphQlItemViewState>> =
        observeGraphQlRequestsUseCase()
            .map { list -> list.map { toUi(it) } }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), emptyList())

    private val clickedRequestId = MutableStateFlow<String?>(null)

    val detailState: StateFlow<GraphQlDetailViewState?> =
        clickedRequestId
            .flatMapLatest { id ->
                if (id == null) {
                    flowOf(null)
                } else {
                    observeGraphQlRequestsByIdUseCase(id)
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

    fun onGraphQlItemUserAction(action: OnGraphQlItemUserAction) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (action) {
                is OnGraphQlItemUserAction.CopyUrl -> {
                    val domainModel =
                        observeGraphQlRequestsByIdUseCase(action.item.uuid).firstOrNull()
                            ?: return@launch
                    copyToClipboard(domainModel.infos.url)
                }

                is OnGraphQlItemUserAction.OnClicked -> {
                    clickedRequestId.update {
                        if (it == action.item.uuid) {
                            null
                        } else {
                            action.item.uuid
                        }
                    }
                }

                is OnGraphQlItemUserAction.Remove -> {
                    removeGraphQlRequestUseCase(requestId = action.item.uuid)
                }

                is OnGraphQlItemUserAction.RemoveLinesAbove -> {
                    removeGraphQlRequestsBeforeUseCase(requestId = action.item.uuid)
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
            resetCurrentDeviceGraphQlRequestsUseCase()
        }
    }
}
