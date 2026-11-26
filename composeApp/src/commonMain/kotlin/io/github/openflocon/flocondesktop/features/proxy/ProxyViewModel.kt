package io.github.openflocon.flocondesktop.features.proxy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.connect.usecase.GetLocalAddressesUseCase
import io.github.openflocon.domain.proxy.usecase.ObserveProxyEnabledUseCase
import io.github.openflocon.domain.proxy.usecase.ObserveProxyStateUseCase
import io.github.openflocon.domain.proxy.usecase.SetProxyEnabledUseCase
import io.github.openflocon.domain.settings.usecase.ObserveMascotEnabledUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.proxy.mapper.mapToProxyUiState
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyMoodUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyStatusUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProxyViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val setProxyEnabledUseCase: SetProxyEnabledUseCase,
    observeProxyEnabledUseCase: ObserveProxyEnabledUseCase,
    observeProxyStateUseCase: ObserveProxyStateUseCase,
    observeMascotEnabledUseCase: ObserveMascotEnabledUseCase,
    getLocalAddressesUseCase: GetLocalAddressesUseCase,
) : ViewModel() {

    /** ip → interface name of the computer, to tell the networks apart in the picker. */
    private val interfaceNames = MutableStateFlow<Map<String, String>>(emptyMap())

    init {
        viewModelScope.launch {
            interfaceNames.value = withContext(dispatcherProvider.viewModel) {
                getLocalAddressesUseCase().associate { it.ip to it.interfaceName }
            }
        }
    }

    val uiState: StateFlow<ProxyUiState> = combine(
        observeProxyEnabledUseCase(),
        observeProxyStateUseCase(),
        observeMascotEnabledUseCase(),
        interfaceNames,
        ::mapToProxyUiState,
    ).stateInWhileSubscribed(
        ProxyUiState(enabled = false, status = ProxyStatusUiModel.Off, mood = ProxyMoodUiModel.Asleep, mascotEnabled = false),
    )

    fun onEnabledChange(enabled: Boolean) {
        viewModelScope.launch {
            withContext(dispatcherProvider.viewModel) { setProxyEnabledUseCase(enabled) }
        }
    }
}
