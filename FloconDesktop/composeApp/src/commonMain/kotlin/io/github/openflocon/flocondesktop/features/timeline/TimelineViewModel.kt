package io.github.openflocon.flocondesktop.features.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.ObserveIsCurrentDeviceIAndAppIsActiveUseCase
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSettingsUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSummaryUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineAutoScrollUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineInvertListUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelinePinPanelUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineSelectedSourcesUseCase
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOptionsDelegate
import io.github.openflocon.flocondesktop.features.timeline.mapper.TimelineDestination
import io.github.openflocon.flocondesktop.features.timeline.mapper.separatorBetween
import io.github.openflocon.flocondesktop.features.timeline.mapper.toDestination
import io.github.openflocon.flocondesktop.features.timeline.mapper.toFilterPanel
import io.github.openflocon.flocondesktop.features.timeline.mapper.toUi
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterPanelUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSettingsUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineSettingsUiModel
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
internal class TimelineViewModel(
    observeTimelineUseCase: ObserveTimelineUseCase,
    observeTimelineSummaryUseCase: ObserveTimelineSummaryUseCase,
    observeIsCurrentDeviceAndAppActiveUseCase: ObserveIsCurrentDeviceIAndAppIsActiveUseCase,
    observeTimelineSettingsUseCase: ObserveTimelineSettingsUseCase,
    private val setTimelineAutoScrollUseCase: SetTimelineAutoScrollUseCase,
    private val setTimelineInvertListUseCase: SetTimelineInvertListUseCase,
    private val setTimelinePinPanelUseCase: SetTimelinePinPanelUseCase,
    private val setTimelineSelectedSourcesUseCase: SetTimelineSelectedSourcesUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val navigationState: MainFloconNavigationState,
    private val networkItemOptionsDelegate: NetworkItemOptionsDelegate,
) : ViewModel() {

    private val _filterText = MutableStateFlow("")
    val filterText: StateFlow<String> = _filterText.asStateFlow()

    // the sources displayed, saved in the settings. Read from the source, not from [selectedSources], to wait for the stored
    // selection instead of loading all the sources first
    private val storedSources: Flow<Set<TimelineSource>> = observeTimelineSettingsUseCase()
        .map { it.selectedSources }
        .distinctUntilChanged()

    val selectedSources: StateFlow<Set<TimelineSource>> = storedSources
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimelineSource.entries.toSet())

    // the device is connected
    val isLive: StateFlow<Boolean> = observeIsCurrentDeviceAndAppActiveUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val settings: StateFlow<TimelineSettingsUiModel> = observeTimelineSettingsUseCase()
        .map { TimelineSettingsUiModel(autoScroll = it.autoScroll, invertList = it.invertList, pinPanel = it.pinPanel) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), previewTimelineSettingsUiModel())

    // the last event opened, highlighted in the list
    private val _selectedKey = MutableStateFlow<String?>(null)
    val selectedKey: StateFlow<String?> = _selectedKey.asStateFlow()

    // the values kept by facet, not saved: they depend on what the app sent
    private val selectedFacets = MutableStateFlow<Map<TimelineFacet, Set<String>>>(emptyMap())

    private val showOnlyWithEvents = MutableStateFlow(false)

    private val _panelOpen = MutableStateFlow(false)
    val panelOpen: StateFlow<Boolean> = _panelOpen.asStateFlow()

    // shared by the events and the counters, so they always describe the same filter
    private val filter: Flow<TimelineFilterDomainModel> = combine(
        // no delay when the text is cleared, a short one while typing
        _filterText.debounce { if (it.isEmpty()) 0L else 300L },
        storedSources,
        selectedFacets,
    ) { text, sources, facets ->
        TimelineFilterDomainModel(
            sources = sources,
            text = text,
            facets = facets,
        )
    }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    val summary: StateFlow<TimelineSummaryUiModel> = filter
        .flatMapLatest { filter ->
            observeTimelineSummaryUseCase(filter).map { it.toUi(selected = filter.sources) }
        }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimelineSummaryUiModel.Empty)

    val filterPanel: StateFlow<TimelineFilterPanelUiModel> = combine(summary, selectedSources, selectedFacets, showOnlyWithEvents) { summary, sources, facets, onlyWithEvents ->
        summary.toFilterPanel(selectedSources = sources, facets = facets, onlyWithEvents = onlyWithEvents)
    }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimelineFilterPanelUiModel.Empty)

    // nothing received at all: the filters are not the reason of the empty list
    val showEmptyState: StateFlow<Boolean> = combine(summary, _filterText) { summary, filterText ->
        filterText.isBlank() && summary.sourceCounts.values.sum() == 0
    }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // the day headers depend on the direction of the list
    // read from the source, not from [settings], to wait for the stored value instead of reloading the list after the default one
    private val invertList: Flow<Boolean> = observeTimelineSettingsUseCase().map { it.invertList }.distinctUntilChanged()

    val items: Flow<PagingData<TimelineItemUiModel>> = combine(filter, invertList) { filter, inverted -> filter to inverted }
        .flatMapLatest { (filter, inverted) ->
            observeTimelineUseCase(filter).map { pagingData ->
                pagingData
                    .map<_, TimelineItemUiModel> { TimelineItemUiModel.Row(it.toUi()) }
                    .insertSeparators { before, after -> separatorBetween(before, after, inverted) }
            }
        }
        .flowOn(dispatcherProvider.viewModel)
        .cachedIn(viewModelScope)

    fun onAction(action: TimelineAction) {
        when (action) {
            is TimelineAction.FilterTextChanged -> _filterText.update { action.text }

            is TimelineAction.ToggleSource -> viewModelScope.launch {
                val current = selectedSources.value
                setTimelineSelectedSourcesUseCase(if (action.source in current) current - action.source else current + action.source)
            }

            is TimelineAction.ToggleFacet -> selectedFacets.update { facets ->
                val values = facets[action.facet].orEmpty()
                val updated = if (action.value in values) values - action.value else values + action.value
                if (updated.isEmpty()) facets - action.facet else facets + (action.facet to updated)
            }

            is TimelineAction.SetOnlyWithEvents -> showOnlyWithEvents.update { action.value }

            is TimelineAction.TogglePanel -> _panelOpen.update { !it }

            is TimelineAction.ClearFilters -> {
                _filterText.update { "" }
                selectedFacets.update { emptyMap() }
                viewModelScope.launch { setTimelineSelectedSourcesUseCase(TimelineSource.entries.toSet()) }
            }

            is TimelineAction.ToggleAutoScroll -> viewModelScope.launch { setTimelineAutoScrollUseCase(action.value) }

            is TimelineAction.InvertList -> viewModelScope.launch { setTimelineInvertListUseCase(action.value) }

            is TimelineAction.PinPanel -> viewModelScope.launch { setTimelinePinPanelUseCase(action.value) }

            is TimelineAction.NetworkOption -> viewModelScope.launch(dispatcherProvider.viewModel) {
                networkItemOptionsDelegate.onOption(callId = action.callId, option = action.option)
            }

            is TimelineAction.Open -> {
                val destination = action.row.toDestination()
                if (destination != null) {
                    _selectedKey.update { action.row.callKey }
                }
                when (destination) {
                    is TimelineDestination.Panel -> navigationState.navigate(destination.route)
                    null -> Unit
                }
            }
        }
    }
}
