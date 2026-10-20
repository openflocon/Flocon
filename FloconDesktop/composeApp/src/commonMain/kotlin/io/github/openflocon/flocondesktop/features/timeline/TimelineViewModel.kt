package io.github.openflocon.flocondesktop.features.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineUseCase
import io.github.openflocon.flocondesktop.app.MenuNavigator
import io.github.openflocon.flocondesktop.features.timeline.mapper.TimelineDestination
import io.github.openflocon.flocondesktop.features.timeline.mapper.dayHeaderBetween
import io.github.openflocon.flocondesktop.features.timeline.mapper.toDestination
import io.github.openflocon.flocondesktop.features.timeline.mapper.toUi
import io.github.openflocon.navigation.MainFloconNavigationState
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
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
import kotlinx.coroutines.flow.update

@OptIn(FlowPreview::class)
internal class TimelineViewModel(
    observeTimelineUseCase: ObserveTimelineUseCase,
    dispatcherProvider: DispatcherProvider,
    private val navigationState: MainFloconNavigationState,
    private val menuNavigator: MenuNavigator,
) : ViewModel() {

    private val _filterText = MutableStateFlow("")
    val filterText: StateFlow<String> = _filterText.asStateFlow()

    private val _selectedSources = MutableStateFlow(TimelineSource.entries.toSet())
    val selectedSources: StateFlow<Set<TimelineSource>> = _selectedSources.asStateFlow()

    val items: Flow<PagingData<TimelineItemUiModel>> = combine(
        // no delay when the text is cleared, a short one while typing
        _filterText.debounce { if (it.isEmpty()) 0L else 300L },
        _selectedSources,
    ) { text, sources ->
        TimelineFilterDomainModel(
            sources = sources,
            text = text,
        )
    }
        .flatMapLatest { filter -> observeTimelineUseCase(filter) }
        .map { pagingData ->
            pagingData
                .map<_, TimelineItemUiModel> { TimelineItemUiModel.Row(it.toUi()) }
                .insertSeparators(generator = ::dayHeaderBetween)
        }
        .flowOn(dispatcherProvider.viewModel)
        .cachedIn(viewModelScope)

    fun onAction(action: TimelineAction) {
        when (action) {
            is TimelineAction.FilterTextChanged -> _filterText.update { action.text }
            is TimelineAction.ToggleSource -> _selectedSources.update { current ->
                if (action.source in current) current - action.source else current + action.source
            }
            is TimelineAction.Open -> when (val destination = action.row.toDestination()) {
                is TimelineDestination.Panel -> navigationState.navigate(destination.route)
                is TimelineDestination.Screen -> menuNavigator.open(destination.screen)
                null -> Unit
            }
        }
    }
}
