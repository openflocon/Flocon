package io.github.openflocon.flocondesktop.features.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.connect.usecase.GetLocalAddressesUseCase
import io.github.openflocon.flocondesktop.features.connect.model.ConnectUiState
import io.github.openflocon.flocondesktop.features.connect.model.LocalAddressUiModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val getLocalAddressesUseCase: GetLocalAddressesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectUiState(addresses = persistentListOf()))
    val uiState: StateFlow<ConnectUiState> = _uiState.asStateFlow()

    /** The network can change while the app is open, so addresses are read again each time the dialog opens. */
    fun refresh() {
        viewModelScope.launch {
            val addresses = withContext(dispatcherProvider.viewModel) {
                getLocalAddressesUseCase().map { LocalAddressUiModel(interfaceName = it.interfaceName, ip = it.ip) }
            }
            _uiState.value = ConnectUiState(addresses = addresses.toImmutableList())
        }
    }
}
