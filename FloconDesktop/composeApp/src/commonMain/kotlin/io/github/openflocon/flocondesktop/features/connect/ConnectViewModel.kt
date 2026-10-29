package io.github.openflocon.flocondesktop.features.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.Constant
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.connect.usecase.GetLocalAddressesUseCase
import io.github.openflocon.domain.settings.usecase.ObserveMascotEnabledUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.connect.model.ConnectUiState
import io.github.openflocon.flocondesktop.features.connect.model.LocalAddressUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val getLocalAddressesUseCase: GetLocalAddressesUseCase,
    observeMascotEnabledUseCase: ObserveMascotEnabledUseCase,
) : ViewModel() {

    private val addresses = MutableStateFlow<ImmutableList<LocalAddressUiModel>>(persistentListOf())

    val uiState: StateFlow<ConnectUiState> = combine(
        addresses,
        observeMascotEnabledUseCase(),
    ) { addresses, mascotEnabled ->
        ConnectUiState(
            addresses = addresses,
            port = Constant.SERVER_WEBSOCKET_PORT,
            mascotEnabled = mascotEnabled,
        )
    }.stateInWhileSubscribed(
        ConnectUiState(
            addresses = persistentListOf(),
            port = Constant.SERVER_WEBSOCKET_PORT,
            mascotEnabled = false,
        ),
    )

    /** The network can change while the app is open, so addresses are read again each time the dialog opens. */
    fun refresh() {
        viewModelScope.launch {
            addresses.value = withContext(dispatcherProvider.viewModel) {
                getLocalAddressesUseCase()
                    .map { LocalAddressUiModel(interfaceName = it.interfaceName, ip = it.ip) }
                    .toImmutableList()
            }
        }
    }
}
