package io.github.openflocon.flocondesktop.features.network.websocket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkWebsocketIdsUseCase
import io.github.openflocon.domain.network.usecase.mocks.SendNetworkWebsocketMockUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NetworkWebsocketMockViewModel(
    private val observeNetworkWebsocketIdsUseCase: ObserveNetworkWebsocketIdsUseCase,
    private val sendNetworkWebsocketMockUseCase: SendNetworkWebsocketMockUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    val clientsIds = observeNetworkWebsocketIdsUseCase()
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = emptyList())

    private val _selectedClientId = MutableStateFlow<String?>(null)
    val selectedClientId = _selectedClientId.asStateFlow()

    fun send(message: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            (_selectedClientId.value ?: clientsIds.value.firstOrNull())?.let { clientId ->
                sendNetworkWebsocketMockUseCase(
                    websocketId = clientId,
                    message = message,
                )
                feedbackDisplayer.displayMessage("sent")
            }
        }
    }

    fun onClientSelected(id: String) {
        _selectedClientId.update { id }
    }
}
