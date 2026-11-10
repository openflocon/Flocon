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
    // words of the path (and query) of the network calls to keep (include) or hide (exclude)
    val networkPathRules: List<TimelineRuleDomainModel> = emptyList(),
    // null: every duration, the calls without duration yet are kept. A range hides them
    val networkDuration: TimelineDurationRangeDomainModel? = null,
    // words of the sql query of the database logs to keep (include) or hide (exclude)
    val databaseRules: List<TimelineRuleDomainModel> = emptyList(),
    // true: the logs of the transactions (BEGIN, COMMIT...) are hidden
    val hideDatabaseTransactions: Boolean = false,
    // false: every log whose type is not reserved (tagged logs, custom types) is hidden
    val logTaggedEnabled: Boolean = true,
    // words of the type of the tagged logs to keep (include) or hide (exclude), the reserved types are never affected
    val logTagRules: List<TimelineRuleDomainModel> = emptyList(),
    // null: every time. Only the events list and the list of the timeline apply the bounds, not the counters
    val fromMs: Long? = null,
    val toMs: Long? = null,
    // null: every event. An empty set matches nothing. A network call is pointed at by its request event
    val events: Set<TimelineEventRef>? = null,
)
