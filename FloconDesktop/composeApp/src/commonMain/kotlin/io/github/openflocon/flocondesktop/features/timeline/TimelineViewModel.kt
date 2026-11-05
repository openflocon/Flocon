package io.github.openflocon.flocondesktop.features.timeline

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveIsCurrentDeviceIAndAppIsActiveUseCase
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.usecase.ConsumeTimelineFocusUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineEventsUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineFocusUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineScreenshotsFailingUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineScreenshotsSupportedUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineScreenshotsUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSettingsUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSummaryUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineAutoScrollUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineGraphUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineInvertListUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelinePinPanelUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineScreenshotIntervalUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineScreenshotsUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineSelectedSourcesUseCase
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOption
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOptionsDelegate
import io.github.openflocon.flocondesktop.features.timeline.graph.GraphFocusUiModel
import io.github.openflocon.flocondesktop.features.timeline.graph.GraphSessionUiModel
import io.github.openflocon.flocondesktop.features.timeline.graph.GraphUiModel
import io.github.openflocon.flocondesktop.features.timeline.graph.buildGraph
import io.github.openflocon.flocondesktop.features.timeline.graph.screenshotsOfSession
import io.github.openflocon.flocondesktop.features.timeline.mapper.TimelineDestination
import io.github.openflocon.flocondesktop.features.timeline.mapper.screenshotMarkerBetween
import io.github.openflocon.flocondesktop.features.timeline.mapper.separatorBetween
import io.github.openflocon.flocondesktop.features.timeline.mapper.toDestination
import io.github.openflocon.flocondesktop.features.timeline.mapper.toUi
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineScreenshotsStatus
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSettingsUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineSettingsUiModel
import io.github.openflocon.navigation.MainFloconNavigationState
import io.github.openflocon.navigation.PanelRoute
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class TimelineViewModel(
    observeTimelineUseCase: ObserveTimelineUseCase,
    observeTimelineSummaryUseCase: ObserveTimelineSummaryUseCase,
    observeTimelineEventsUseCase: ObserveTimelineEventsUseCase,
    observeIsCurrentDeviceAndAppActiveUseCase: ObserveIsCurrentDeviceIAndAppIsActiveUseCase,
    observeTimelineSettingsUseCase: ObserveTimelineSettingsUseCase,
    observeTimelineScreenshotsFailingUseCase: ObserveTimelineScreenshotsFailingUseCase,
    observeTimelineScreenshotsSupportedUseCase: ObserveTimelineScreenshotsSupportedUseCase,
    observeTimelineScreenshotsUseCase: ObserveTimelineScreenshotsUseCase,
    private val setTimelineAutoScrollUseCase: SetTimelineAutoScrollUseCase,
    private val setTimelineInvertListUseCase: SetTimelineInvertListUseCase,
    private val setTimelinePinPanelUseCase: SetTimelinePinPanelUseCase,
    private val setTimelineGraphUseCase: SetTimelineGraphUseCase,
    private val setTimelineScreenshotsUseCase: SetTimelineScreenshotsUseCase,
    private val setTimelineScreenshotIntervalUseCase: SetTimelineScreenshotIntervalUseCase,
    private val setTimelineSelectedSourcesUseCase: SetTimelineSelectedSourcesUseCase,
    private val filterStore: TimelineFilterStore,
    private val dispatcherProvider: DispatcherProvider,
    private val navigationState: MainFloconNavigationState,
    private val networkItemOptionsDelegate: NetworkItemOptionsDelegate,
    observeTimelineFocusUseCase: ObserveTimelineFocusUseCase,
    private val consumeTimelineFocusUseCase: ConsumeTimelineFocusUseCase,
    observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) : ViewModel() {

    val filterText: StateFlow<String> = filterStore.text

    // the device is connected
    val isLive: StateFlow<Boolean> = observeIsCurrentDeviceAndAppActiveUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val settings: StateFlow<TimelineSettingsUiModel> = observeTimelineSettingsUseCase()
        .map {
            TimelineSettingsUiModel(
                autoScroll = it.autoScroll,
                invertList = it.invertList,
                pinPanel = it.pinPanel,
                graph = it.graph,
                screenshots = it.screenshots,
                screenshotIntervalSeconds = it.screenshotIntervalSeconds,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), previewTimelineSettingsUiModel())

    // the last screenshots failed, or the device cannot take them: the control of the header says it
    val screenshotsStatus: StateFlow<TimelineScreenshotsStatus> = combine(
        observeTimelineScreenshotsFailingUseCase(),
        observeTimelineScreenshotsSupportedUseCase(),
    ) { failing, supported ->
        when {
            !supported -> TimelineScreenshotsStatus.Unavailable
            failing -> TimelineScreenshotsStatus.Failing
            else -> TimelineScreenshotsStatus.Working
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimelineScreenshotsStatus.Working)

    // the last event opened, highlighted in the list
    val selectedKey: StateFlow<String?>
        field = MutableStateFlow<String?>(null)

    // the filter panel is a panel of the navigation, opened from the screen
    val filterPanelOpen: StateFlow<Boolean> = snapshotFlow { navigationState.stack.lastOrNull { it is PanelRoute } is TimelineRoutes.Filters }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // what the button of the filter panel counts: the values selected and the sources hidden
    val activeFilterCount: StateFlow<Int> = combine(
        filterStore.facets,
        filterStore.storedSources,
        filterStore.currentSessionOnly,
        filterStore.network,
        filterStore.database,
    ) { facets, sources, currentSessionOnly, network, database ->
        facets.values.sumOf { it.size } + TimelineSource.entries.count { it !in sources } + network.ruleCount + database.activeCount +
            if (currentSessionOnly) 1 else 0
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val filter: Flow<TimelineFilterDomainModel> = filterStore.filter

    val summary: StateFlow<TimelineSummaryUiModel> = filter
        .flatMapLatest { filter ->
            observeTimelineSummaryUseCase(filter).map { it.toUi(selected = filter.sources) }
        }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimelineSummaryUiModel.Empty)

    // the session of the graph, by start of the app: null is the latest one
    private val graphSession = MutableStateFlow<Long?>(null)

    // the starts of the sessions displayed, oldest first
    private val sessionStarts: Flow<List<Long>> = summary
        .map { it.sessionEventCounts.keys.sorted() }
        .distinctUntilChanged()

    val graphSessionPosition: StateFlow<GraphSessionUiModel?> = combine(sessionStarts, graphSession) { starts, selected ->
        if (starts.isEmpty()) null else GraphSessionUiModel(position = starts.indexOf(selected ?: starts.last()) + 1, total = starts.size)
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // one session at a time, only computed while the graph is displayed. The updates are batched: a burst of events
    // does not pack the lanes once per event
    @OptIn(FlowPreview::class)
    val graph: StateFlow<GraphUiModel> = settings
        .map { it.graph }
        .distinctUntilChanged()
        .flatMapLatest { enabled ->
            if (!enabled) {
                flowOf(GraphUiModel.Empty)
            } else {
                combine(filter, sessionStarts, graphSession) { filter, starts, selected -> Triple(filter, starts, selected) }
                    .flatMapLatest { (filter, starts, selected) ->
                        val sessionStart = (selected?.takeIf { it in starts } ?: starts.lastOrNull())
                        val nextSessionStart = sessionStart?.let { start -> starts.firstOrNull { it > start } }
                        combine(
                            observeTimelineEventsUseCase(filter.copy(appInstance = sessionStart)).sample(GRAPH_BATCH_MS),
                            observeTimelineScreenshotsUseCase(),
                        ) { events, shots ->
                            val start = sessionStart ?: events.minOfOrNull { it.timestamp } ?: 0L
                            // a session that is not known has no screenshot to place
                            val screenshots = if (sessionStart == null) emptyList() else screenshotsOfSession(shots, start, nextSessionStart)
                            buildGraph(events, start).copy(screenshots = screenshots)
                        }
                    }
            }
        }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GraphUiModel.Empty)

    // the zone of the session the graph is asked to show (by the MCP), until the graph has shown it
    val graphFocus: StateFlow<GraphFocusUiModel?>
        field = MutableStateFlow<GraphFocusUiModel?>(null)

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            appliedFocusRequests(observeTimelineFocusUseCase(), observeCurrentDeviceIdAndPackageNameUseCase()).collectResilient(
                action = { request ->
                    // the sessions come with the summary of the device: the one of the request is waited for, what is displayed is the fallback
                    val starts = awaitSessionStarts(request, sessionStarts, FOCUS_SESSIONS_WAIT_MS)
                    val plan = planFocus(request, starts, graphShown = observeTimelineSettingsUseCase().first().graph)
                    plan.selectedKey?.let { key -> selectedKey.value = key }
                    plan.graph?.let { graph ->
                        graphSession.value = graph.session
                        graphFocus.value = graph.focus
                    }
                    consumeTimelineFocusUseCase(request)
                },
                // a request that fails is dropped: the next ones are still applied
                onError = { request, _ -> consumeTimelineFocusUseCase(request) },
            )
        }
    }

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
            // the events of the filter are shared (cachedIn) to be combined with each new screenshot without a new query. The scope
            // lives as long as this filter: it is cancelled when the filter changes
            channelFlow {
                val events = observeTimelineUseCase(filter)
                    .map { pagingData -> pagingData.map<_, TimelineItemUiModel> { TimelineItemUiModel.Row(it.toUi()) } }
                    .cachedIn(this)
                combine(events, observeTimelineScreenshotsUseCase()) { pagingData, shots ->
                    // without screenshot the list is the one it always was
                    val withScreenshots = if (shots.isEmpty()) {
                        pagingData
                    } else {
                        pagingData.insertSeparators { before, after -> screenshotMarkerBetween(before, after, shots) }
                    }
                    withScreenshots.insertSeparators { before, after -> separatorBetween(before, after, inverted) }
                }.collect { send(it) }
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

            is TimelineAction.SetGraph -> viewModelScope.launch { setTimelineGraphUseCase(action.value) }

            is TimelineAction.SetScreenshots -> viewModelScope.launch { setTimelineScreenshotsUseCase(action.value) }

            is TimelineAction.SetScreenshotInterval -> viewModelScope.launch { setTimelineScreenshotIntervalUseCase(action.seconds) }

            is TimelineAction.MoveGraphSession -> viewModelScope.launch {
                val starts = summary.value.sessionEventCounts.keys.sorted()
                val current = starts.indexOf(graphSession.value ?: starts.lastOrNull())
                val target = (current + action.delta).coerceIn(0, (starts.size - 1).coerceAtLeast(0))
                // the latest session follows the new ones
                graphSession.value = if (target == starts.lastIndex) null else starts.getOrNull(target)
            }

            is TimelineAction.GraphFocusApplied -> graphFocus.value = null

            is TimelineAction.NetworkOption -> viewModelScope.launch(dispatcherProvider.viewModel) {
                networkItemOptionsDelegate.onOption(callId = action.callId, option = action.option)
            }

            is TimelineAction.Exclude -> filterStore.hideFacetValue(action.facet, action.value)

            is TimelineAction.ExcludePath -> filterStore.hidePath(action.path)

            is TimelineAction.OpenFilters -> {
                filterStore.focusSource(action.source)
                if (!filterPanelOpen.value) navigationState.navigate(TimelineRoutes.Filters)
            }

            is TimelineAction.CopySelectedUrl -> {
                // the key of a call is "Network:<id>", see TimelineRowUiModel.callKey
                val prefix = "${TimelineSource.Network.name}:"
                selectedKey.value?.takeIf { it.startsWith(prefix) }?.removePrefix(prefix)?.let { callId ->
                    viewModelScope.launch(dispatcherProvider.viewModel) {
                        networkItemOptionsDelegate.onOption(callId = callId, option = NetworkItemOption.CopyUrl)
                    }
                }
            }

            is TimelineAction.ExcludeSource -> viewModelScope.launch {
                // read from the settings each time, like the filter panel
                setTimelineSelectedSourcesUseCase(filterStore.storedSources.first() - action.source)
            }

            is TimelineAction.Open -> {
                val destination = action.row.toDestination()
                if (destination != null) {
                    selectedKey.update { action.row.callKey }
                }
                when (destination) {
                    is TimelineDestination.Panel -> navigationState.navigate(destination.route)
                    null -> Unit
                }
            }
        }
    }
}

// the graph is rebuilt at most this often while events arrive
private const val GRAPH_BATCH_MS = 100L

// how long the sessions of a device are waited for before a focus request, when the one of the request is not known yet
private const val FOCUS_SESSIONS_WAIT_MS = 1_500L
