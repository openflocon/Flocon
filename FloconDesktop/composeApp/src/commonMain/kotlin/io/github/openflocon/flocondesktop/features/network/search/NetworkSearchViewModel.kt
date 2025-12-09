package io.github.openflocon.flocondesktop.features.network.search

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.SearchScope
import io.github.openflocon.domain.network.usecase.search.SearchNetworkCallsUseCase
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.flocondesktop.features.network.list.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.search.model.NetworkSearchUiState
import io.github.openflocon.library.designsystem.common.asState
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class NetworkSearchViewModel(
    private val searchNetworkCallsUseCase: SearchNetworkCallsUseCase,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val navigationState: MainFloconNavigationState,
) : ViewModel(), KoinComponent {

    private val _query = mutableStateOf("")
    val query = _query.asState()
    private val _selectedScopes = MutableStateFlow(SearchScope.entries.toSet())
    private val _loading = MutableStateFlow(false)

    val uiState: StateFlow<NetworkSearchUiState> = combines(
        snapshotFlow { _query.value },
        _selectedScopes,
        observeCurrentDeviceIdAndPackageNameUseCase(),
        _loading
    ).flatMapLatest { (query, scopes, deviceInfo, loading) ->
        flow {
            val results = searchNetworkCallsUseCase(query, scopes)
            emit(
                NetworkSearchUiState(
                    selectedScopes = scopes,
                    results = results.map { it.toUi(deviceInfo) },
                    loading = false
                )
            )
        }
    }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NetworkSearchUiState()
        )

    fun onQueryChanged(query: String) {
        _query.value = query
        _loading.value = true
    }

    @OptIn(ExperimentalUuidApi::class)
    fun onNavigateToDetail(requestId: String) {
        navigationState.navigate(
            NetworkRoutes.WindowDetail(
                requestId,
                Uuid.random().toString()
            )
        )
    }

    fun onScopeToggled(scope: SearchScope) {
        _selectedScopes.update { current ->
            if (current.contains(scope)) {
                current - scope
            } else {
                current + scope
            }
        }
        _loading.value = true
    }
}
