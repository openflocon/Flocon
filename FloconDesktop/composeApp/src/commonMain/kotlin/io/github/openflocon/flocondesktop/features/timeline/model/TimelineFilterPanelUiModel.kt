package io.github.openflocon.flocondesktop.features.timeline.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
internal data class TimelineFacetValueUiModel(
    val value: String,
    val count: Int,
    val selected: Boolean,
    val method: NetworkMethodUi? = null, // an http method, displayed like in the network screen
    val httpFamily: Int? = null, // the first digit of a status class, to color it like in the network screen
    val logLevel: TimelineLogLevel? = null, // a level of log, to color it like in the list
    val logType: String? = null, // a type of log: the reserved ones show their glyph like in the list
)

@Immutable
internal data class TimelineFacetGroupUiModel(
    val facet: TimelineFacet,
    val values: ImmutableList<TimelineFacetValueUiModel>,
    // the values are displayed as tags side by side, like the methods and the status of the network screen
    val asTags: Boolean = false,
)

// one source of the filter panel: its switch and the facets to narrow it down
@Immutable
internal data class TimelineFilterSectionUiModel(
    val source: TimelineSource,
    val count: Int,
    val enabled: Boolean,
    val activeFacetCount: Int,
    val groups: ImmutableList<TimelineFacetGroupUiModel>,
)

// an include or exclude rule typed by the user, displayed as a chip
@Immutable
internal data class TimelineRuleUiModel(
    val value: String,
    val exclude: Boolean,
    val active: Boolean = true, // an inactive rule stays in the list but is not applied
)

/**
 * The network card of the filter panel: the filters of the network calls, side by side instead of in dropdowns.
 * The duration is told by positions of the slider: a position is the start of a bucket of the histogram, the last one is "no limit".
 */
@Immutable
internal data class TimelineNetworkFilterUiModel(
    val enabled: Boolean,
    val isFiltered: Boolean, // a value is hidden or a rule is set: offers to reset
    val matchingCount: Int, // the network calls displayed
    val totalCount: Int, // the network calls received, with the text of the filter
    val methods: ImmutableList<TimelineFacetValueUiModel>,
    val statusClasses: ImmutableList<TimelineFacetValueUiModel>,
    val statusRules: ImmutableList<TimelineRuleUiModel>,
    val histogram: ImmutableList<Int>, // calls of each bucket of duration
    val pendingCount: Int, // calls without duration yet
    val durationStart: Int,
    val durationEnd: Int,
    val durationLabel: String?, // null: every duration
    val content: ImmutableList<TimelineFacetValueUiModel>,
    val hosts: ImmutableList<TimelineFacetValueUiModel>, // the hosts matching the rules
    val hostRules: ImmutableList<TimelineRuleUiModel>,
    val hiddenHostCount: Int,
    val hostCount: Int,
) {
    val hasHiddenStatus: Boolean get() = statusClasses.any { !it.selected }
    val maxDurationPosition: Int get() = histogram.size

    companion object {
        val Empty = TimelineNetworkFilterUiModel(
            enabled = true,
            isFiltered = false,
            matchingCount = 0,
            totalCount = 0,
            methods = persistentListOf(),
            statusClasses = persistentListOf(),
            statusRules = persistentListOf(),
            histogram = persistentListOf(),
            pendingCount = 0,
            durationStart = 0,
            durationEnd = 0,
            durationLabel = null,
            content = persistentListOf(),
            hosts = persistentListOf(),
            hostRules = persistentListOf(),
            hiddenHostCount = 0,
            hostCount = 0,
        )
    }
}

internal fun previewTimelineNetworkFilterUiModel() = TimelineNetworkFilterUiModel(
    enabled = true,
    isFiltered = true,
    matchingCount = 8,
    totalCount = 22,
    methods = persistentListOf(
        TimelineFacetValueUiModel("GET", count = 5, selected = true, method = NetworkMethodUi.Http.GET),
        TimelineFacetValueUiModel("POST", count = 2, selected = true, method = NetworkMethodUi.Http.POST),
        TimelineFacetValueUiModel("DELETE", count = 0, selected = false, method = NetworkMethodUi.Http.DELETE),
        TimelineFacetValueUiModel("WS", count = 1, selected = true, method = NetworkMethodUi.WebSocket),
        TimelineFacetValueUiModel("GRPC", count = 0, selected = true, method = NetworkMethodUi.Grpc),
    ),
    statusClasses = persistentListOf(
        TimelineFacetValueUiModel("1xx", count = 1, selected = true, httpFamily = 1),
        TimelineFacetValueUiModel("2xx", count = 5, selected = true, httpFamily = 2),
        TimelineFacetValueUiModel("3xx", count = 0, selected = true, httpFamily = 3),
        TimelineFacetValueUiModel("4xx", count = 0, selected = true, httpFamily = 4),
        TimelineFacetValueUiModel("5xx", count = 2, selected = true, httpFamily = 5),
    ),
    statusRules = persistentListOf(TimelineRuleUiModel("101", exclude = false), TimelineRuleUiModel("500-503", exclude = true)),
    histogram = persistentListOf(0, 1, 4, 8, 6, 2, 3, 1, 0, 0),
    pendingCount = 14,
    durationStart = 2,
    durationEnd = 7,
    durationLabel = "100 ms - 1 s",
    content = persistentListOf(
        TimelineFacetValueUiModel("WEBSOCKET", count = 22, selected = true),
        TimelineFacetValueUiModel("IMAGE", count = 0, selected = true),
        TimelineFacetValueUiModel("OTHER", count = 0, selected = true),
    ),
    hosts = persistentListOf(
        TimelineFacetValueUiModel("ws.postman-echo.com:443", count = 14, selected = true),
        TimelineFacetValueUiModel("ws.postman-echo.com", count = 8, selected = true),
    ),
    hostRules = persistentListOf(TimelineRuleUiModel("postman", exclude = false), TimelineRuleUiModel("analytics", exclude = true)),
    hiddenHostCount = 0,
    hostCount = 2,
)

// one entry of the types menu of the logs card
@Immutable
internal data class TimelineLogTypeUiModel(
    val type: String?, // a reserved type ("navigation"...), null: the tagged logs, every log whose type is not reserved
    val count: Int,
    val selected: Boolean,
)

/**
 * The logs card of the filter panel: the types menu, the tag rules and the levels.
 * The tag and the levels only apply to the tagged logs, never to the reserved types.
 */
@Immutable
internal data class TimelineLogFilterUiModel(
    val types: ImmutableList<TimelineLogTypeUiModel>, // the reserved types, then the tagged logs
    val allTypesSelected: Boolean,
    val totalCount: Int,
    val taggedEnabled: Boolean,
    val tagQuery: String,
    val tagSuggestions: ImmutableList<TimelineFacetValueUiModel>, // the known tags matching the query
    val tagIncludes: ImmutableList<TimelineRuleUiModel>,
    val tagExcludes: ImmutableList<TimelineRuleUiModel>,
    val levels: ImmutableList<TimelineFacetValueUiModel>,
) {
    companion object {
        val Empty = TimelineLogFilterUiModel(
            types = persistentListOf(),
            allTypesSelected = true,
            totalCount = 0,
            taggedEnabled = true,
            tagQuery = "",
            tagSuggestions = persistentListOf(),
            tagIncludes = persistentListOf(),
            tagExcludes = persistentListOf(),
            levels = persistentListOf(),
        )
    }
}

internal fun previewTimelineLogFilterUiModel(
    allTypesSelected: Boolean = true,
    taggedEnabled: Boolean = true,
) = TimelineLogFilterUiModel(
    types = persistentListOf(
        TimelineLogTypeUiModel("navigation", count = 12, selected = true),
        TimelineLogTypeUiModel("userAction", count = 30, selected = allTypesSelected),
        TimelineLogTypeUiModel("lifecycle", count = 8, selected = allTypesSelected),
        TimelineLogTypeUiModel(null, count = 41, selected = taggedEnabled),
    ),
    allTypesSelected = allTypesSelected && taggedEnabled,
    totalCount = 91,
    taggedEnabled = taggedEnabled,
    tagQuery = "bi",
    tagSuggestions = persistentListOf(
        TimelineFacetValueUiModel("billing", count = 23, selected = true, logType = "billing"),
        TimelineFacetValueUiModel("bin", count = 4, selected = true, logType = "bin"),
    ),
    tagIncludes = persistentListOf(TimelineRuleUiModel("payment", exclude = false), TimelineRuleUiModel("auth", exclude = false, active = false)),
    tagExcludes = persistentListOf(TimelineRuleUiModel("debug", exclude = true)),
    levels = persistentListOf(
        TimelineFacetValueUiModel("DEBUG", count = 10, selected = true, logLevel = TimelineLogLevel.Debug),
        TimelineFacetValueUiModel("INFO", count = 20, selected = false, logLevel = TimelineLogLevel.Info),
        TimelineFacetValueUiModel("WARNING", count = 6, selected = true, logLevel = TimelineLogLevel.Warning),
        TimelineFacetValueUiModel("ERROR", count = 5, selected = true, logLevel = TimelineLogLevel.Error),
    ),
)

@Immutable
internal data class TimelineFilterPanelUiModel(
    val sections: ImmutableList<TimelineFilterSectionUiModel>,
    val network: TimelineNetworkFilterUiModel,
    val logs: TimelineLogFilterUiModel,
    val currentSessionOnly: Boolean,
    // something is filtered: used to offer to clear it
    val hasActiveFilters: Boolean,
    // facets selected, or sources hidden: the badge of the button opening the panel
    val activeFilterCount: Int,
) {
    companion object {
        val Empty = TimelineFilterPanelUiModel(
            sections = persistentListOf(),
            network = TimelineNetworkFilterUiModel.Empty,
            logs = TimelineLogFilterUiModel.Empty,
            currentSessionOnly = false,
            hasActiveFilters = false,
            activeFilterCount = 0,
        )
    }
}
