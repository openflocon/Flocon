package io.github.openflocon.flocondesktop.features.timeline

import androidx.compose.runtime.snapshotFlow
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
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSettingsUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSummaryUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineAutoScrollUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineInvertListUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelinePinPanelUseCase
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOptionsDelegate
import io.github.openflocon.flocondesktop.features.timeline.mapper.TimelineDestination
import io.github.openflocon.flocondesktop.features.timeline.mapper.separatorBetween
import io.github.openflocon.flocondesktop.features.timeline.mapper.toDestination
import io.github.openflocon.flocondesktop.features.timeline.mapper.toUi
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSettingsUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineSettingsUiModel
import io.github.openflocon.navigation.MainFloconNavigationState
import io.github.openflocon.navigation.PanelRoute
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class TimelineViewModel(
    observeTimelineUseCase: ObserveTimelineUseCase,
    observeTimelineSummaryUseCase: ObserveTimelineSummaryUseCase,
    observeIsCurrentDeviceAndAppActiveUseCase: ObserveIsCurrentDeviceIAndAppIsActiveUseCase,
    observeTimelineSettingsUseCase: ObserveTimelineSettingsUseCase,
    private val setTimelineAutoScrollUseCase: SetTimelineAutoScrollUseCase,
    private val setTimelineInvertListUseCase: SetTimelineInvertListUseCase,
    private val setTimelinePinPanelUseCase: SetTimelinePinPanelUseCase,
    private val filterStore: TimelineFilterStore,
    private val dispatcherProvider: DispatcherProvider,
    private val navigationState: MainFloconNavigationState,
    private val networkItemOptionsDelegate: NetworkItemOptionsDelegate,
) : ViewModel() {

    val filterText: StateFlow<String> = filterStore.text

    // the device is connected
    val isLive: StateFlow<Boolean> = observeIsCurrentDeviceAndAppActiveUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val settings: StateFlow<TimelineSettingsUiModel> = observeTimelineSettingsUseCase()
        .map { TimelineSettingsUiModel(autoScroll = it.autoScroll, invertList = it.invertList, pinPanel = it.pinPanel) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), previewTimelineSettingsUiModel())

    // the last event opened, highlighted in the list
    private val _selectedKey = MutableStateFlow<String?>(null)
    val selectedKey: StateFlow<String?> = _selectedKey.asStateFlow()

    // the filter panel is a panel of the navigation, opened from the screen
    val filterPanelOpen: StateFlow<Boolean> = snapshotFlow { navigationState.stack.lastOrNull { it is PanelRoute } is TimelineRoutes.Filters }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // what the button of the filter panel counts: the values selected and the sources hidden
    val activeFilterCount: StateFlow<Int> = combine(
        filterStore.facets,
        filterStore.storedSources,
        filterStore.currentSessionOnly,
    ) { facets, sources, currentSessionOnly ->
        facets.values.sumOf { it.size } + TimelineSource.entries.count { it !in sources } + if (currentSessionOnly) 1 else 0
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val filter: Flow<TimelineFilterDomainModel> = filterStore.filter

    val summary: StateFlow<TimelineSummaryUiModel> = filter
        .flatMapLatest { filter ->
            observeTimelineSummaryUseCase(filter).map { it.toUi(selected = filter.sources) }
        }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimelineSummaryUiModel.Empty)

    // nothing received at all: the filters are not the reason of the empty list
    val showEmptyState: StateFlow<Boolean> = combine(summary, filterStore.text) { summary, filterText ->
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
            is TimelineAction.FilterTextChanged -> filterStore.setText(action.text)

            is TimelineAction.ToggleFilterPanel -> if (filterPanelOpen.value) {
                navigationState.closePanel()
            } else {
                navigationState.navigate(TimelineRoutes.Filters)
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
