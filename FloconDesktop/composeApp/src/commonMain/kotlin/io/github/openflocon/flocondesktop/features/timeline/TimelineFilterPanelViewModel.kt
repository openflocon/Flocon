package io.github.openflocon.flocondesktop.features.timeline

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.timeline.models.TIMELINE_DURATION_BUCKETS_MS
import io.github.openflocon.domain.timeline.models.TimelineDurationRangeDomainModel
import io.github.openflocon.domain.timeline.models.TimelineRuleDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.models.parseTimelineStatusRules
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSummaryUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineSelectedSourcesUseCase
import io.github.openflocon.flocondesktop.common.utils.launchMainMolecule
import io.github.openflocon.flocondesktop.features.timeline.mapper.toFilterPanel
import io.github.openflocon.flocondesktop.features.timeline.mapper.toUi
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterPanelUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class TimelineFilterPanelViewModel(
    observeTimelineSummaryUseCase: ObserveTimelineSummaryUseCase,
    private val setTimelineSelectedSourcesUseCase: SetTimelineSelectedSourcesUseCase,
    private val filterStore: TimelineFilterStore,
) : ViewModel() {

    // the counts follow the text but not the facets: ticking a value must not make the others, or the sources, move or disappear
    private val summary: StateFlow<TimelineSummaryUiModel> = filterStore.filter
        .map {
            it.copy(
                facets = emptyMap(),
                networkStatusRules = emptyList(),
                networkHostRules = emptyList(),
                networkPathRules = emptyList(),
                networkDuration = null,
                databaseRules = emptyList(),
                hideDatabaseTransactions = false,
                logTaggedEnabled = true,
                logTagRules = emptyList(),
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

    // what is typed in the tag field of the logs card: the field reads it synchronously, it is not part of the panel
    private val logTagInput = TimelineLogTagInput()
    val logTagText: State<String> get() = logTagInput.text

    val panel: StateFlow<TimelineFilterPanelUiModel> = viewModelScope.launchMainMolecule {
        val summary by summary.collectAsState()

        // read from the settings: the panel is never empty, the switches settle once the stored selection is known
        val sources by filterStore.storedSources.collectAsState(initial = null)
        val facets by filterStore.facets.collectAsState()
        val currentSessionOnly by filterStore.currentSessionOnly.collectAsState()
        val network by filterStore.network.collectAsState()
        val database by filterStore.database.collectAsState()
        val logs by filterStore.logs.collectAsState()
        val logTagQuery by logTagInput.query.collectAsState()
        val matchingNetworkCount by matchingNetworkCount.collectAsState()

        val selectedSources = sources ?: TimelineSource.entries.toSet()
        remember(summary, selectedSources, facets, currentSessionOnly, network, database, logs, logTagQuery, matchingNetworkCount) {
            summary.toFilterPanel(
                selectedSources = selectedSources,
                facets = facets,
                currentSessionOnly = currentSessionOnly,
                network = network,
                database = database,
                matchingNetworkCount = matchingNetworkCount,
                logs = logs,
                logTagQuery = logTagQuery,
            )
        }
    }

    val focusedSource: StateFlow<TimelineSource?> = filterStore.focusedSource

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

            is TimelineFilterAction.AddPathRule -> {
                action.text.trim().takeIf { it.isNotEmpty() }?.let { filterStore.addPathRule(TimelineRuleDomainModel(it, action.exclude)) }
            }

            is TimelineFilterAction.RemovePathRule -> filterStore.removePathRule(action.value)

            is TimelineFilterAction.AddDatabaseRule -> {
                action.text.trim().takeIf { it.isNotEmpty() }?.let { filterStore.addDatabaseRule(TimelineRuleDomainModel(it, action.exclude)) }
            }

            is TimelineFilterAction.RemoveDatabaseRule -> filterStore.removeDatabaseRule(action.value)

            is TimelineFilterAction.SetHideDatabaseTransactions -> filterStore.setHideDatabaseTransactions(action.value)

            is TimelineFilterAction.SetLogTaggedEnabled -> filterStore.setLogTaggedEnabled(action.value)

            is TimelineFilterAction.AddLogTagRule -> {
                action.text.trim().takeIf { it.isNotEmpty() }?.let {
                    filterStore.addLogTagRule(it, action.exclude)
                    logTagInput.clear()
                }
            }

            is TimelineFilterAction.SetLogTagRuleActive -> filterStore.setLogTagRuleActive(action.value, action.active)

            is TimelineFilterAction.RemoveLogTagRule -> filterStore.removeLogTagRule(action.value)

            is TimelineFilterAction.SetLogTagQuery -> logTagInput.set(action.text)

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

            is TimelineFilterAction.FocusHandled -> filterStore.focusSource(null)

            is TimelineFilterAction.ClearAll -> {
                filterStore.clear()
                logTagInput.clear()
                viewModelScope.launch { setTimelineSelectedSourcesUseCase(TimelineSource.entries.toSet()) }
            }
        }
    }
}
