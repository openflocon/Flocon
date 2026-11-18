package io.github.openflocon.flocondesktop.features.webview

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.webview.usecase.ClearAllWebViewEventsUseCase
import io.github.openflocon.domain.webview.usecase.ObserveWebViewEventsUseCase
import io.github.openflocon.flocondesktop.features.webview.mapper.mapToUi
import io.github.openflocon.flocondesktop.features.webview.mapper.toDomain
import io.github.openflocon.flocondesktop.features.webview.model.WebViewAction
import io.github.openflocon.flocondesktop.features.webview.model.WebViewEventUiModel
import io.github.openflocon.flocondesktop.features.webview.model.WebViewFilterUiModel
import io.github.openflocon.library.designsystem.common.asState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class WebViewViewModel(
    private val observeWebViewEventsUseCase: ObserveWebViewEventsUseCase,
    private val clearAllWebViewEventsUseCase: ClearAllWebViewEventsUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _filterText = mutableStateOf("")
    val filterText: State<String> = _filterText.asState()

    private val _selectedFilter = MutableStateFlow(WebViewFilterUiModel.All)
    val selectedFilter: StateFlow<WebViewFilterUiModel> = _selectedFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val events: Flow<PagingData<WebViewEventUiModel>> = combine(
        snapshotFlow { _filterText.value }.debounce(150),
        _selectedFilter,
    ) { text, filter -> text to filter }
        .flatMapLatest { (text, filter) ->
            observeWebViewEventsUseCase(
                kind = filter.toDomain(),
                filter = text,
            )
        }
        .map { pagingData -> pagingData.map { it.mapToUi() } }
        .flowOn(dispatcherProvider.viewModel)
        .cachedIn(viewModelScope)

    fun onFilterTextChanged(value: String) {
        _filterText.value = value
    }

    fun onFilterSelected(filter: WebViewFilterUiModel) {
        _selectedFilter.value = filter
    }

    fun onAction(action: WebViewAction) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (action) {
                WebViewAction.Clear -> clearAllWebViewEventsUseCase()
            }
        }
    }
}
