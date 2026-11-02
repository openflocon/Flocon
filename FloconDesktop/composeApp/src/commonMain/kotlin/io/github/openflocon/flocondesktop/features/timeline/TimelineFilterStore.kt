package io.github.openflocon.flocondesktop.features.timeline

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import io.github.openflocon.domain.timeline.models.TimelineDurationRangeDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineRuleDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update

/**
 * What the user typed or moved in the network card of the filter panel, next to the values hidden by facet.
 */
internal data class TimelineNetworkRules(
    val enabled: Boolean = true,
    val statusRules: List<TimelineRuleDomainModel> = emptyList(),
    val hostRules: List<TimelineRuleDomainModel> = emptyList(),
    val duration: TimelineDurationRangeDomainModel? = null,
) {
    val ruleCount: Int get() = statusRules.size + hostRules.size + if (duration != null) 1 else 0
}

/**
 * What the user set in the database card: words of the sql query to keep or hide, and the transactions.
 */
internal data class TimelineDatabaseRules(
    val rules: List<TimelineRuleDomainModel> = emptyList(),
    val hideTransactions: Boolean = false,
) {
    // what the badge of the filter button counts
    val activeCount: Int get() = rules.size + if (hideTransactions) 1 else 0
}

/**
 * The filter of the timeline, shared by the screen and by its filter panel: they are two destinations, so two view models.
 * The sources are a setting of the timeline, the text and the facets are not saved.
 */
@OptIn(FlowPreview::class)
internal class TimelineFilterStore(
    observeTimelineSettingsUseCase: ObserveTimelineSettingsUseCase,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val text: StateFlow<String>
        field = MutableStateFlow("")

    // the values kept by facet
    val facets: StateFlow<Map<TimelineFacet, Set<String>>>
        field = MutableStateFlow<Map<TimelineFacet, Set<String>>>(emptyMap())

    // the status, host and duration rules of the network calls
    val network: StateFlow<TimelineNetworkRules>
        field = MutableStateFlow(TimelineNetworkRules())

    // the sql rules and the transactions of the database logs
    val database: StateFlow<TimelineDatabaseRules>
        field = MutableStateFlow(TimelineDatabaseRules())

    // the events of the latest session only
    val currentSessionOnly: StateFlow<Boolean>
        field = MutableStateFlow(false)

    // read from the settings to wait for the stored selection instead of loading all the sources first
    val storedSources = observeTimelineSettingsUseCase()
        .map { it.selectedSources }
        .distinctUntilChanged()

    // no delay when the text is cleared, a short one while typing
    private val debouncedText: Flow<String> = text.debounce { if (it.isEmpty()) 0L else 300L }

    // shared by the events and the counters, so they always describe the same filter
    val filter: Flow<TimelineFilterDomainModel> = moleculeFlow(RecompositionMode.Immediate) {
        // the current text as initial value: a new subscription must not flash an empty text before the debounce emits
        val text by debouncedText.collectAsState(initial = text.value)
        val sources by storedSources.collectAsState(initial = null)
        val facets by facets.collectAsState()
        val currentSessionOnly by currentSessionOnly.collectAsState()
        val network by network.collectAsState()
        val database by database.collectAsState()

        sources?.let { selected ->
            TimelineFilterDomainModel(
                sources = selected,
                text = text,
                facets = facets,
                currentSessionOnly = currentSessionOnly,
                networkEnabled = network.enabled,
                networkStatusRules = network.statusRules,
                networkHostRules = network.hostRules,
                networkDuration = network.duration,
                databaseRules = database.rules,
                hideDatabaseTransactions = database.hideTransactions,
            )
        }
    }
        // nothing until the stored selection is known
        .filterNotNull()
        .distinctUntilChanged()
        .shareIn(scope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    fun setText(value: String) {
        text.update { value }
    }

    fun toggleFacet(facet: TimelineFacet, value: String) {
        facets.update { facets ->
            val values = facets[facet].orEmpty()
            val updated = if (value in values) values - value else values + value
            if (updated.isEmpty()) facets - facet else facets + (facet to updated)
        }
    }

    // hides one more value, ex: from the menu of a row. Adding it twice changes nothing, unlike toggleFacet
    fun hideFacetValue(facet: TimelineFacet, value: String) {
        facets.update { it + (facet to it[facet].orEmpty() + value) }
        // the filters of the network calls are ignored while its card is off: the value would stay displayed
        if (facet.source == TimelineSource.Network) setNetworkEnabled(true)
    }

    // the values hidden of a facet at once, ex: only one host, or none
    fun setHiddenValues(facet: TimelineFacet, values: Set<String>) {
        facets.update { if (values.isEmpty()) it - facet else it + (facet to values) }
    }

    // the values hidden of the network facets and the rules, the master switch is kept
    fun resetNetwork() {
        facets.update { facets -> facets.filterKeys { it.source != TimelineSource.Network } }
        network.update { TimelineNetworkRules(enabled = it.enabled) }
    }

    fun setNetworkEnabled(value: Boolean) {
        network.update { it.copy(enabled = value) }
    }

    // a rule is told by its text: adding it again only changes its side, include or exclude
    fun addStatusRule(rule: TimelineRuleDomainModel) {
        network.update { it.copy(statusRules = it.statusRules.filterNot { existing -> existing.value == rule.value } + rule) }
    }

    fun removeStatusRule(value: String) {
        network.update { it.copy(statusRules = it.statusRules.filterNot { existing -> existing.value == value }) }
    }

    fun addHostRule(rule: TimelineRuleDomainModel) {
        network.update { it.copy(hostRules = it.hostRules.filterNot { existing -> existing.value == rule.value } + rule) }
    }

    fun removeHostRule(value: String) {
        network.update { it.copy(hostRules = it.hostRules.filterNot { existing -> existing.value == value }) }
    }

    fun setNetworkDuration(value: TimelineDurationRangeDomainModel?) {
        network.update { it.copy(duration = value) }
    }

    // a rule is told by its text: adding it again only changes its side, include or exclude
    fun addDatabaseRule(rule: TimelineRuleDomainModel) {
        database.update { it.copy(rules = it.rules.filterNot { existing -> existing.value == rule.value } + rule) }
    }

    fun removeDatabaseRule(value: String) {
        database.update { it.copy(rules = it.rules.filterNot { existing -> existing.value == value }) }
    }

    fun setHideDatabaseTransactions(value: Boolean) {
        database.update { it.copy(hideTransactions = value) }
    }

    fun setCurrentSessionOnly(value: Boolean) {
        currentSessionOnly.update { value }
    }

    // the text, the facets and the session, the sources are a setting
    fun clear() {
        text.update { "" }
        facets.update { emptyMap() }
        network.update { TimelineNetworkRules() }
        database.update { TimelineDatabaseRules() }
        currentSessionOnly.update { false }
    }
}
