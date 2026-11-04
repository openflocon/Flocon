package io.github.openflocon.flocondesktop.features.timeline

import io.github.openflocon.domain.timeline.models.TimelineDurationRangeDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineRuleDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.repository.TimelineUserFilterProvider
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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
 * A word of the type of the tagged logs typed in the logs card: kept ([exclude] false) or hidden ([exclude] true).
 * An inactive rule stays in the list but is not applied.
 */
internal data class TimelineLogTagRule(
    val value: String,
    val exclude: Boolean,
    val active: Boolean = true,
)

/**
 * What the user chose in the logs card of the filter panel, next to the types and levels hidden by facet.
 * [taggedEnabled] false hides every log whose type is not reserved (tagged logs, custom types).
 */
internal data class TimelineLogRules(
    val taggedEnabled: Boolean = true,
    val tagRules: List<TimelineLogTagRule> = emptyList(),
) {
    val activeRules: List<TimelineRuleDomainModel>
        get() = tagRules.filter { it.active }.map { TimelineRuleDomainModel(it.value, it.exclude) }
}

/**
 * The filter of the timeline, shared by the screen and by its filter panel: they are two destinations, so two view models.
 * The sources are a setting of the timeline, the text and the facets are not saved.
 */
@OptIn(FlowPreview::class)
internal class TimelineFilterStore(
    observeTimelineSettingsUseCase: ObserveTimelineSettingsUseCase,
) : TimelineUserFilterProvider {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val text: StateFlow<String>
        field = MutableStateFlow("")

    // the values kept by facet
    val facets: StateFlow<Map<TimelineFacet, Set<String>>>
        field = MutableStateFlow<Map<TimelineFacet, Set<String>>>(emptyMap())

    // the status, host and duration rules of the network calls
    val network: StateFlow<TimelineNetworkRules>
        field = MutableStateFlow(TimelineNetworkRules())

    // the tagged logs switch and the tag rules of the logs
    val logs: StateFlow<TimelineLogRules>
        field = MutableStateFlow(TimelineLogRules())

    // the events of the latest session only
    val currentSessionOnly: StateFlow<Boolean>
        field = MutableStateFlow(false)

    // read from the settings to wait for the stored selection instead of loading all the sources first
    val storedSources = observeTimelineSettingsUseCase()
        .map { it.selectedSources }
        .distinctUntilChanged()

    // shared by the events and the counters, so they always describe the same filter
    val filter: Flow<TimelineFilterDomainModel> = combine(
        // no delay when the text is cleared, a short one while typing
        text.debounce { if (it.isEmpty()) 0L else 300L },
        storedSources,
        facets,
        currentSessionOnly,
        combine(network, logs) { network, logs -> network to logs },
    ) { text, sources, facets, currentSessionOnly, (network, logs) ->
        TimelineFilterDomainModel(
            sources = sources,
            text = text,
            facets = facets,
            currentSessionOnly = currentSessionOnly,
            networkEnabled = network.enabled,
            networkStatusRules = network.statusRules,
            networkHostRules = network.hostRules,
            networkDuration = network.duration,
            logTaggedEnabled = logs.taggedEnabled,
            logTagRules = logs.activeRules,
        )
    }
        .shareIn(scope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    // the filter as it is now, without the delay of the text: [filter] has no value until the timeline is displayed
    override suspend fun current(): TimelineFilterDomainModel = TimelineFilterDomainModel(
        sources = storedSources.first(),
        text = text.value,
        facets = facets.value,
        currentSessionOnly = currentSessionOnly.value,
        networkEnabled = network.value.enabled,
        networkStatusRules = network.value.statusRules,
        networkHostRules = network.value.hostRules,
        networkDuration = network.value.duration,
        logTaggedEnabled = logs.value.taggedEnabled,
        logTagRules = logs.value.activeRules,
    )

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

    fun setLogTaggedEnabled(value: Boolean) {
        logs.update { it.copy(taggedEnabled = value) }
    }

    // a rule is told by its text: adding it again only changes its side and activates it
    fun addLogTagRule(value: String, exclude: Boolean) {
        logs.update { it.copy(tagRules = it.tagRules.filterNot { existing -> existing.value == value } + TimelineLogTagRule(value, exclude)) }
    }

    fun setLogTagRuleActive(value: String, active: Boolean) {
        logs.update { logs -> logs.copy(tagRules = logs.tagRules.map { if (it.value == value) it.copy(active = active) else it }) }
    }

    fun removeLogTagRule(value: String) {
        logs.update { it.copy(tagRules = it.tagRules.filterNot { existing -> existing.value == value }) }
    }

    fun setCurrentSessionOnly(value: Boolean) {
        currentSessionOnly.update { value }
    }

    // the text, the facets and the session, the sources are a setting
    fun clear() {
        text.update { "" }
        facets.update { emptyMap() }
        network.update { TimelineNetworkRules() }
        logs.update { TimelineLogRules() }
        currentSessionOnly.update { false }
    }
}
