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
import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.responseBody
import io.github.openflocon.domain.network.models.responseHeaders
import io.github.openflocon.domain.network.usecase.ObserveNetworkRequestsByIdUseCase
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class NetworkSearchViewModel(
    private val searchNetworkCallsUseCase: SearchNetworkCallsUseCase,
    private val observeNetworkRequestsByIdUseCase: ObserveNetworkRequestsByIdUseCase,
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
                    results = results.map { it.toUi(null /* don't check old instance here*/) },
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

    private val _selectedRequestId = MutableStateFlow<String?>(null)
    val selectedRequestId = _selectedRequestId.asStateFlow()

    val selectedRequest: StateFlow<FloconNetworkCallDomainModel?> = _selectedRequestId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else observeNetworkRequestsByIdUseCase(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches = _matches.asStateFlow()

    private val _currentMatchIndex = MutableStateFlow(0)
    val currentMatchIndex = _currentMatchIndex.asStateFlow()

    init {
        // Calculate matches when query or selected request changes
        viewModelScope.launch {
            combine(snapshotFlow { _query.value }, selectedRequest) { query, request ->
                if (query.isBlank() || request == null) {
                    emptyList()
                } else {
                    val regex = query.toRegex(RegexOption.IGNORE_CASE)
                    val matches = mutableListOf<Match>()

                    // URL
                    val url = request.request.url
                    regex.findAll(url).forEach {
                        matches.add(Match(it.range.first, it.range.last - it.range.first + 1, MatchLocation.Url, url))
                    }

                    // Method
                    val method = request.request.method
                    regex.findAll(method).forEach {
                        matches.add(Match(it.range.first, it.range.last - it.range.first + 1, MatchLocation.Method, method))
                    }

                    // Status
                    val status = request.response?.statusFormatted
                    if (status != null) {
                        regex.findAll(status).forEach {
                            matches.add(Match(it.range.first, it.range.last - it.range.first + 1, MatchLocation.Status, status))
                        }
                    }

                    // Request Headers
                    val reqHeaders = request.request.headers.entries.joinToString("\n") { "${it.key}: ${it.value}" }
                    regex.findAll(reqHeaders).forEach {
                        matches.add(Match(it.range.first, it.range.last - it.range.first + 1, MatchLocation.RequestHeaders, reqHeaders))
                    }

                    // Request Body
                    val reqBody = request.request.body
                    if (reqBody != null) {
                        regex.findAll(reqBody).forEach {
                            matches.add(Match(it.range.first, it.range.last - it.range.first + 1, MatchLocation.RequestBody, reqBody))
                        }
                    }

                    // Response Headers
                    val resHeaders = request.responseHeaders()?.entries?.joinToString("\n") { "${it.key}: ${it.value}" }
                    if (resHeaders != null) {
                        regex.findAll(resHeaders).forEach {
                            matches.add(Match(it.range.first, it.range.last - it.range.first + 1, MatchLocation.ResponseHeaders, resHeaders))
                        }
                    }

                    // Response Body
                    val resBody = request.responseBody()
                    if (resBody != null) {
                        regex.findAll(resBody).forEach {
                            matches.add(Match(it.range.first, it.range.last - it.range.first + 1, MatchLocation.ResponseBody, resBody))
                        }
                    }
                    
                    matches
                }
            }.collect {
                _matches.value = it
                _currentMatchIndex.value = 0
            }
        }
    }

    fun onSelectRequest(requestId: String) {
        _selectedRequestId.value = requestId
    }

    fun onNextMatch() {
        if (_matches.value.isNotEmpty()) {
            _currentMatchIndex.update { (it + 1) % _matches.value.size }
        }
    }

    fun onPrevMatch() {
        if (_matches.value.isNotEmpty()) {
            _currentMatchIndex.update {
                if (it - 1 < 0) _matches.value.lastIndex else it - 1
            }
        }
    }

    fun onClosePreview() {
        _selectedRequestId.value = null
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

@Immutable
data class Match(
    val start: Int,
    val length: Int,
    val location: MatchLocation,
    val content: String
)

enum class MatchLocation(val label: String) {
    Url("URL"),
    Method("Method"),
    Status("Status"),
    RequestHeaders("Request Headers"),
    RequestBody("Request Body"),
    ResponseHeaders("Response Headers"),
    ResponseBody("Response Body")
}
