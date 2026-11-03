package io.github.openflocon.flocondesktop.features.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.timeline.models.TIMELINE_DURATION_BUCKETS_MS
import io.github.openflocon.domain.timeline.models.TimelineDurationRangeDomainModel
import io.github.openflocon.domain.timeline.models.TimelineRuleDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.models.parseTimelineStatusRules
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSummaryUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineSelectedSourcesUseCase
import io.github.openflocon.flocondesktop.features.timeline.mapper.toFilterPanel
import io.github.openflocon.flocondesktop.features.timeline.mapper.toUi
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterPanelUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class TimelineFilterPanelViewModel(
    observeTimelineSummaryUseCase: ObserveTimelineSummaryUseCase,
    private val setTimelineSelectedSourcesUseCase: SetTimelineSelectedSourcesUseCase,
    private val filterStore: TimelineFilterStore,
    dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    // the counts follow the text but not the facets: ticking a value must not make the others, or the sources, move or disappear
    private val summary: StateFlow<TimelineSummaryUiModel> = filterStore.filter
        .map {
            it.copy(
                facets = emptyMap(),
                networkStatusRules = emptyList(),
                networkHostRules = emptyList(),
                networkDuration = null,
            )
        }
        .distinctUntilChanged()
        .flatMapLatest { filter ->
            observeTimelineSummaryUseCase(filter).map { it.toUi(selected = filter.sources) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimelineSummaryUiModel.Empty)

    // the network calls the filters let through, for the counter of the network card
    private val matchingNetworkCount: StateFlow<Int?> = filterStore.filter
        .distinctUntilChanged()
        .flatMapLatest { filter ->
            observeTimelineSummaryUseCase(filter).map { it.sources[TimelineSource.Network]?.count ?: 0 }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val panel: StateFlow<TimelineFilterPanelUiModel> = combine(
        summary,
        filterStore.storedSources,
        filterStore.facets,
        filterStore.currentSessionOnly,
        combine(filterStore.network, matchingNetworkCount) { network, matching -> network to matching },
    ) { summary, sources, facets, currentSessionOnly, (network, matching) ->
        summary.toFilterPanel(
            selectedSources = sources,
            facets = facets,
            currentSessionOnly = currentSessionOnly,
            network = network,
            matchingNetworkCount = matching,
        )
    }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimelineFilterPanelUiModel.Empty)

    fun onAction(action: TimelineFilterAction) {
        when (action) {
            is TimelineFilterAction.ToggleSource -> viewModelScope.launch {
                // read from the settings each time: a state flow nobody collects keeps its first value, the source could never come back
                val current = filterStore.storedSources.first()
                setTimelineSelectedSourcesUseCase(if (action.source in current) current - action.source else current + action.source)
            }

            is TimelineFilterAction.ToggleFacet -> filterStore.toggleFacet(action.facet, action.value)

            is TimelineFilterAction.SetCurrentSessionOnly -> filterStore.setCurrentSessionOnly(action.value)

            is TimelineFilterAction.SetNetworkEnabled -> filterStore.setNetworkEnabled(action.value)

            is TimelineFilterAction.ResetNetwork -> filterStore.resetNetwork()

            is TimelineFilterAction.SetHiddenValues -> filterStore.setHiddenValues(action.facet, action.values)

            is TimelineFilterAction.AddStatusRules -> {
                // a text with a part that is not a status is not a rule
                parseTimelineStatusRules(action.text)?.let { matchers ->
                    matchers.forEach { filterStore.addStatusRule(TimelineRuleDomainModel(it.label(), action.exclude)) }
                }
            }

            is TimelineFilterAction.RemoveStatusRule -> filterStore.removeStatusRule(action.value)

            is TimelineFilterAction.AddHostRule -> {
                action.text.trim().takeIf { it.isNotEmpty() }?.let { filterStore.addHostRule(TimelineRuleDomainModel(it, action.exclude)) }
            }

            is TimelineFilterAction.RemoveHostRule -> filterStore.removeHostRule(action.value)

            is TimelineFilterAction.SetDurationRange -> {
                val buckets = TIMELINE_DURATION_BUCKETS_MS
                val range = if (action.start <= 0 && action.end >= buckets.size) {
                    null // every duration
                } else {
                    TimelineDurationRangeDomainModel(
                        minMs = buckets[action.start.coerceIn(0, buckets.lastIndex)],
                        maxMs = buckets.getOrNull(action.end),
                    )
                }
                filterStore.setNetworkDuration(range)
            }

            is TimelineFilterAction.ClearAll -> {
                filterStore.clear()
                viewModelScope.launch { setTimelineSelectedSourcesUseCase(TimelineSource.entries.toSet()) }
            }
        }
    }
}
