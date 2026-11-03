package io.github.openflocon.domain.timeline.models

import io.github.openflocon.domain.device.models.AppInstance

data class TimelineFilterDomainModel(
    val sources: Set<TimelineSource> = TimelineSource.entries.toSet(),
    val text: String? = null,
    val appInstance: AppInstance? = null, // null: every session
    // only the events since the latest start of the app, the other sessions are hidden
    val currentSessionOnly: Boolean = false,
    // the values hidden by facet, a facet without value hides nothing. The facets of an unselected source are ignored
    val facets: Map<TimelineFacet, Set<String>> = emptyMap(),
    // false: the filters of the network calls are all ignored
    val networkEnabled: Boolean = true,
    // codes or ranges ("404", "500-503") of the network calls to keep (include) or hide (exclude)
    val networkStatusRules: List<TimelineRuleDomainModel> = emptyList(),
    // words of the host of the network calls to keep (include) or hide (exclude)
    val networkHostRules: List<TimelineRuleDomainModel> = emptyList(),
    // null: every duration, the calls without duration yet are kept. A range hides them
    val networkDuration: TimelineDurationRangeDomainModel? = null,
)
