package io.github.openflocon.flocondesktop.features.grpc.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.copyToClipboard
import io.github.openflocon.flocondesktop.features.grpc.domain.DeleteGrpcCallBeforeUseCase
import io.github.openflocon.flocondesktop.features.grpc.domain.DeleteGrpcCallUseCase
import io.github.openflocon.flocondesktop.features.grpc.domain.ObserveGrpcCallByIdUseCase
import io.github.openflocon.flocondesktop.features.grpc.domain.ObserveGrpcCallsUseCase
import io.github.openflocon.flocondesktop.features.grpc.domain.ResetCurrentDeviceGrpcCallsUseCase
import io.github.openflocon.flocondesktop.features.grpc.ui.mapper.toDetailUi
import io.github.openflocon.flocondesktop.features.grpc.ui.mapper.toUi
import io.github.openflocon.flocondesktop.features.grpc.ui.model.GrpcDetailViewState
import io.github.openflocon.flocondesktop.features.grpc.ui.model.GrpcItemViewState
import io.github.openflocon.flocondesktop.features.grpc.ui.model.OnGrpcItemUserAction
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

class GRPCViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    observeGrpcCallsUseCase: ObserveGrpcCallsUseCase,
    private val observeGrpcCallByIdUseCase: ObserveGrpcCallByIdUseCase,
    private val deleteGrpcCallBeforeUseCase: DeleteGrpcCallBeforeUseCase,
    private val deleteGrpcCallUseCase: DeleteGrpcCallUseCase,
    private val resetCurrentDeviceGrpcCallsUseCase: ResetCurrentDeviceGrpcCallsUseCase,
) : ViewModel() {

    val state: StateFlow<List<GrpcItemViewState>> =
        observeGrpcCallsUseCase()
            .map { list -> list.map { toUi(it) } }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), emptyList())

    private val clickedCallId = MutableStateFlow<String?>(null)

    val detailState: StateFlow<GrpcDetailViewState?> =
        clickedCallId
            .flatMapLatest { id ->
                if (id == null) {
                    flowOf(null)
                } else {
                    observeGrpcCallByIdUseCase(id)
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

    fun onGrpcItemUserAction(action: OnGrpcItemUserAction) {
        viewModelScope.launch {
            when (action) {
                is OnGrpcItemUserAction.OnClicked -> {
                    clickedCallId.update {
                        if (it == action.item.callId) {
                            null
                        } else {
                            action.item.callId
                        }
                    }
                }

                is OnGrpcItemUserAction.Remove -> {
                    deleteGrpcCallUseCase(callId = action.item.callId)
                }

                is OnGrpcItemUserAction.RemoveLinesAbove -> {
                    deleteGrpcCallBeforeUseCase(callId = action.item.callId)
                }

                is OnGrpcItemUserAction.CopyMethod -> {
                    val domainModel = observeGrpcCallByIdUseCase(action.item.callId).firstOrNull()
                        ?: return@launch
                    copyToClipboard(domainModel.request.method)
                }

                is OnGrpcItemUserAction.CopyUrl -> {
                    val domainModel = observeGrpcCallByIdUseCase(action.item.callId).firstOrNull()
                        ?: return@launch
                    copyToClipboard(domainModel.request.authority)
                }
            }
        }
    }

    fun onCopyText(text: String) {
        copyToClipboard(text)
        feedbackDisplayer.displayMessage("copied")
    }

    fun closeDetailPanel() {
        viewModelScope.launch {
            clickedCallId.update { null }
        }
    }

    fun onReset() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            resetCurrentDeviceGrpcCallsUseCase()
        }
    }
}
