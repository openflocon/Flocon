package io.github.openflocon.flocondesktop.features.timeline.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineSource
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
)

@Immutable
internal data class TimelineFacetGroupUiModel(
    val facet: TimelineFacet,
    val values: ImmutableList<TimelineFacetValueUiModel>,
    // the values are displayed as tags side by side, like the methods and the status of the network screen
    val asTags: Boolean = false,
)

@Immutable
sealed interface TimelineFilterSectionUi {
    val source: TimelineSource
    val count: Int
    val enabled: Boolean
    val activeFacetCount: Int
}

// the sources without any facet: only the switch
@Immutable
internal data class TimelineBasicFilterUiModel(
    override val source: TimelineSource,
    override val count: Int,
    override val enabled: Boolean,
    override val activeFacetCount: Int = 0,
) : TimelineFilterSectionUi

// the databases and the kinds of statement, ex: only the SELECT of app.db
@Immutable
internal data class TimelineDatabaseFilterUiModel(
    override val count: Int,
    override val enabled: Boolean,
    override val activeFacetCount: Int,
    val names: TimelineFacetGroupUiModel, // listed even before a database is seen: the selector explains it
    val statements: TimelineFacetGroupUiModel?,
    val rules: ImmutableList<TimelineRuleUiModel>, // words of the sql query to keep or hide
    val hideTransactions: Boolean,
) : TimelineFilterSectionUi {
    override val source: TimelineSource = TimelineSource.Database
}

// the tables the analytics events are sent to
@Immutable
internal data class TimelineAnalyticsFilterUiModel(
    override val count: Int,
    override val enabled: Boolean,
    override val activeFacetCount: Int,
    val tables: TimelineFacetGroupUiModel?,
) : TimelineFilterSectionUi {
    override val source: TimelineSource = TimelineSource.Analytics
}

// the tables the items were added to
@Immutable
internal data class TimelineTableFilterUiModel(
    override val count: Int,
    override val enabled: Boolean,
    override val activeFacetCount: Int,
    val tables: TimelineFacetGroupUiModel, // listed even before a table is seen: the selector explains it
) : TimelineFilterSectionUi {
    override val source: TimelineSource = TimelineSource.Table
}

// the kinds of event of the WebViews: console, page, bridge
@Immutable
internal data class TimelineWebViewFilterUiModel(
    override val count: Int,
    override val enabled: Boolean,
    override val activeFacetCount: Int,
    val kinds: TimelineFacetGroupUiModel?,
) : TimelineFilterSectionUi {
    override val source: TimelineSource = TimelineSource.WebView
}

// the classes of exception of the crashes
@Immutable
internal data class TimelineCrashFilterUiModel(
    override val count: Int,
    override val enabled: Boolean,
    override val activeFacetCount: Int,
    val exceptions: TimelineFacetGroupUiModel?,
) : TimelineFilterSectionUi {
    override val source: TimelineSource = TimelineSource.Crash
}

// the kinds of accessibility event
@Immutable
internal data class TimelineAccessibilityFilterUiModel(
    override val count: Int,
    override val enabled: Boolean,
    override val activeFacetCount: Int,
    val actions: TimelineFacetGroupUiModel?,
) : TimelineFilterSectionUi {
    override val source: TimelineSource = TimelineSource.Accessibility
}

// the facets a source can be narrowed down by, to know if it has something to unfold and what a search can match
internal fun TimelineFilterSectionUi.facetGroups(): List<TimelineFacetGroupUiModel> = when (this) {
    is TimelineDatabaseFilterUiModel -> listOfNotNull(names, statements)
    is TimelineTableFilterUiModel -> listOf(tables)
    is TimelineWebViewFilterUiModel -> listOfNotNull(kinds)
    is TimelineAnalyticsFilterUiModel -> listOfNotNull(tables)
    is TimelineCrashFilterUiModel -> listOfNotNull(exceptions)
    is TimelineAccessibilityFilterUiModel -> listOfNotNull(actions)
    is TimelineBasicFilterUiModel -> emptyList()
    is TimelineNetworkFilterUiModel -> emptyList() // has its own card
}

// an include or exclude rule typed by the user, displayed as a chip
@Immutable
internal data class TimelineRuleUiModel(
    val value: String,
    val exclude: Boolean,
)

/**
 * The network card of the filter panel: the filters of the network calls, side by side instead of in dropdowns.
 * The duration is told by positions of the slider: a position is the start of a bucket of the histogram, the last one is "no limit".
 */
@Immutable
internal data class TimelineNetworkFilterUiModel(
    override val count: Int,
    override val enabled: Boolean,
    override val activeFacetCount: Int,
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
) : TimelineFilterSectionUi {
    override val source: TimelineSource = TimelineSource.Network
    val hasHiddenStatus: Boolean get() = statusClasses.any { !it.selected }
    val maxDurationPosition: Int get() = histogram.size
}

internal fun previewTimelineNetworkFilterUiModel() = TimelineNetworkFilterUiModel(
    enabled = true,
    isFiltered = true,
    matchingCount = 8,
    totalCount = 22,
    count = 22,
    activeFacetCount = 0,
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

@Immutable
internal data class TimelineFilterPanelUiModel(
    val sections: ImmutableList<TimelineFilterSectionUi>,
    val currentSessionOnly: Boolean,
    val hasActiveFilters: Boolean,
    val activeFilterCount: Int
)
