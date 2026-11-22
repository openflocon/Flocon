package io.github.openflocon.flocondesktop.features.proxy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.proxy.usecase.ObserveProxyEnabledUseCase
import io.github.openflocon.domain.proxy.usecase.ObserveProxyStateUseCase
import io.github.openflocon.domain.proxy.usecase.SetProxyEnabledUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.proxy.mapper.mapToProxyUiState
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyStatusUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProxyViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val setProxyEnabledUseCase: SetProxyEnabledUseCase,
    observeProxyEnabledUseCase: ObserveProxyEnabledUseCase,
    observeProxyStateUseCase: ObserveProxyStateUseCase,
) : ViewModel() {

    val uiState: StateFlow<ProxyUiState> = combine(
        observeProxyEnabledUseCase(),
        observeProxyStateUseCase(),
        ::mapToProxyUiState,
    ).stateInWhileSubscribed(ProxyUiState(enabled = false, status = ProxyStatusUiModel.Off))

    fun onEnabledChange(enabled: Boolean) {
        viewModelScope.launch {
            withContext(dispatcherProvider.viewModel) { setProxyEnabledUseCase(enabled) }
        }
    }
}
