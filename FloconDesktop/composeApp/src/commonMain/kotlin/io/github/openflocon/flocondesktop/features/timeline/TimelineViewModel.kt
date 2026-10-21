package io.github.openflocon.flocondesktop.features.timeline

import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.ObserveIsCurrentDeviceIAndAppIsActiveUseCase
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSummaryUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineUseCase
import io.github.openflocon.flocondesktop.app.MenuNavigator
import io.github.openflocon.flocondesktop.features.timeline.mapper.TimelineDestination
import io.github.openflocon.flocondesktop.features.timeline.mapper.separatorBetween
import io.github.openflocon.flocondesktop.features.timeline.mapper.toDestination
import io.github.openflocon.flocondesktop.features.timeline.mapper.toUi
import io.github.openflocon.navigation.MainFloconNavigationState
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(FlowPreview::class)
internal class TimelineViewModel(
    observeTimelineUseCase: ObserveTimelineUseCase,
    observeTimelineSummaryUseCase: ObserveTimelineSummaryUseCase,
    observeIsCurrentDeviceAndAppActiveUseCase: ObserveIsCurrentDeviceIAndAppIsActiveUseCase,
    dispatcherProvider: DispatcherProvider,
    private val navigationState: MainFloconNavigationState,
    private val menuNavigator: MenuNavigator,
) : ViewModel() {

    private val _filterText = MutableStateFlow("")
    val filterText: StateFlow<String> = _filterText.asStateFlow()

    private val _selectedSources = MutableStateFlow(TimelineSource.entries.toSet())
    val selectedSources: StateFlow<Set<TimelineSource>> = _selectedSources.asStateFlow()

    // the device is connected
    val isLive: StateFlow<Boolean> = observeIsCurrentDeviceAndAppActiveUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // the last event opened, highlighted in the list
    private val _selectedKey = MutableStateFlow<String?>(null)
    val selectedKey: StateFlow<String?> = _selectedKey.asStateFlow()

    // shared by the events and the counters, so they always describe the same filter
    private val filter: Flow<TimelineFilterDomainModel> = combine(
        // no delay when the text is cleared, a short one while typing
        _filterText.debounce { if (it.isEmpty()) 0L else 300L },
        _selectedSources,
    ) { text, sources ->
        TimelineFilterDomainModel(
            sources = sources,
            text = text,
        )
    }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    val summary: StateFlow<TimelineSummaryUiModel> = filter
        .flatMapLatest { filter ->
            observeTimelineSummaryUseCase(filter).map { it.toUi(selected = filter.sources) }
        }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimelineSummaryUiModel.Empty)

    // nothing received at all: the filters are not the reason of the empty list
    val showEmptyState: StateFlow<Boolean> = combine(summary, _filterText) { summary, filterText ->
        filterText.isBlank() && summary.sourceCounts.values.sum() == 0
    }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val items: Flow<PagingData<TimelineItemUiModel>> = filter
        .flatMapLatest { filter ->
            observeTimelineUseCase(filter).map { pagingData ->
                pagingData
                    .map<_, TimelineItemUiModel> { TimelineItemUiModel.Row(it.toUi()) }
                    .insertSeparators { before, after -> separatorBetween(before, after) }
            }
        }
        .flowOn(dispatcherProvider.viewModel)
        .cachedIn(viewModelScope)

    fun onAction(action: TimelineAction) {
        when (action) {
            is TimelineAction.FilterTextChanged -> _filterText.update { action.text }
            is TimelineAction.ToggleSource -> _selectedSources.update { current ->
                if (action.source in current) current - action.source else current + action.source
            }
            is TimelineAction.Open -> {
                val destination = action.row.toDestination()
                if (destination != null) {
                    _selectedKey.update { action.row.key }
                }
                when (destination) {
                    is TimelineDestination.Panel -> navigationState.navigate(destination.route)
                    is TimelineDestination.Screen -> menuNavigator.open(destination.screen)
                    null -> Unit
                }
            }
        }
    }
}
