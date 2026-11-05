package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TIMELINE_DURATION_BUCKETS_MS
import io.github.openflocon.domain.timeline.models.TIMELINE_RESERVED_LOG_TYPES
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineRuleDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.network.list.mapper.toHttpMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.timeline.TimelineDatabaseRules
import io.github.openflocon.flocondesktop.features.timeline.TimelineLogRules
import io.github.openflocon.flocondesktop.features.timeline.TimelineNetworkRules
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAccessibilityFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAnalyticsFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineBasicFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineCrashFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineDatabaseFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFacetGroupUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFacetValueUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterPanelUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterSectionUi
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineLogFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineLogTypeUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineNetworkFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRuleUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineTableFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineWebViewFilterUiModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * The sources with events come first. Every known value of a facet is listed, even without event.
 * All the values are checked by default: [facets] holds the values the user unchecked.
 */
internal fun TimelineSummaryUiModel.toFilterPanel(
    selectedSources: Set<TimelineSource>,
    facets: Map<TimelineFacet, Set<String>>, // the values hidden, by facet
    currentSessionOnly: Boolean = false,
    network: TimelineNetworkRules = TimelineNetworkRules(),
    database: TimelineDatabaseRules = TimelineDatabaseRules(),
    matchingNetworkCount: Int? = null, // the network calls displayed, null: all of them
    logs: TimelineLogRules = TimelineLogRules(),
    logTagQuery: String = "", // what is typed in the tag field of the logs card
): TimelineFilterPanelUiModel {
    val logRuleCount = (if (logs.taggedEnabled) 0 else 1) + logs.tagRules.count { it.active }
    val sections = TimelineSource.entries
        .filterNot { it == TimelineSource.Network }
        .map { source ->
            toSourceFilter(source, enabled = source in selectedSources, facets = facets, database = database, logRuleCount = logRuleCount)
        }
    val hiddenSources = TimelineSource.entries.count { it !in selectedSources }
    val activeFilterCount = facets.values.sumOf { it.size } + hiddenSources + network.ruleCount + database.activeCount + logRuleCount +
        if (currentSessionOnly) 1 else 0

    return TimelineFilterPanelUiModel(
        sections = sections.plus(toNetworkFilter(facets, network, matchingNetworkCount))
            .sortedBy { it.source.ordinal }
            .toImmutableList(),
        logs = toLogFilter(facets, logs, logTagQuery),
        currentSessionOnly = currentSessionOnly,
        hasActiveFilters = activeFilterCount > 0,
        activeFilterCount = activeFilterCount,
    )
}

// the filter of a source other than the network: each one has its own model, with the facets that make sense for it
private fun TimelineSummaryUiModel.toSourceFilter(
    source: TimelineSource,
    enabled: Boolean,
    facets: Map<TimelineFacet, Set<String>>,
    database: TimelineDatabaseRules,
    logRuleCount: Int,
): TimelineFilterSectionUi {
    fun group(facet: TimelineFacet) = toGroup(facet, hidden = facets[facet].orEmpty())

    // a selector that always exists, even when no value was seen yet
    fun groupOrEmpty(facet: TimelineFacet) = group(facet) ?: TimelineFacetGroupUiModel(facet = facet, values = persistentListOf())
    val count = sourceCounts[source] ?: 0

    // the rules and the transactions are filters of the database source too, the tag rules of the logs card those of the logs
    val activeFacetCount = TimelineFacet.entries.filter { it.source == source }.sumOf { facets[it].orEmpty().size } +
        when (source) {
            TimelineSource.Database -> database.activeCount
            TimelineSource.Log -> logRuleCount
            else -> 0
        }

    return when (source) {
        TimelineSource.Database -> TimelineDatabaseFilterUiModel(
            count = count,
            enabled = enabled,
            activeFacetCount = activeFacetCount,
            names = groupOrEmpty(TimelineFacet.DatabaseName),
            statements = group(TimelineFacet.DatabaseStatement),
            rules = database.rules.map { TimelineRuleUiModel(it.value, it.exclude) }.toImmutableList(),
            hideTransactions = database.hideTransactions,
        )

        TimelineSource.Table -> TimelineTableFilterUiModel(
            count = count,
            enabled = enabled,
            activeFacetCount = activeFacetCount,
            tables = groupOrEmpty(TimelineFacet.TableName),
        )

        TimelineSource.WebView -> TimelineWebViewFilterUiModel(
            count = count,
            enabled = enabled,
            activeFacetCount = activeFacetCount,
            kinds = group(TimelineFacet.WebViewKind),
        )

        TimelineSource.Analytics -> TimelineAnalyticsFilterUiModel(
            count = count,
            enabled = enabled,
            activeFacetCount = activeFacetCount,
            tables = group(TimelineFacet.AnalyticsTable),
        )

        TimelineSource.Crash -> TimelineCrashFilterUiModel(
            count = count,
            enabled = enabled,
            activeFacetCount = activeFacetCount,
            exceptions = group(TimelineFacet.CrashException),
        )

        TimelineSource.Accessibility -> TimelineAccessibilityFilterUiModel(
            count = count,
            enabled = enabled,
            activeFacetCount = activeFacetCount,
            actions = group(TimelineFacet.AccessibilityAction),
        )

        else -> TimelineBasicFilterUiModel(
            source = source,
            count = count,
            enabled = enabled,
            activeFacetCount = activeFacetCount,
        )
    }
}

private const val MAX_TAG_SUGGESTIONS = 8

private val RESERVED_LOG_TYPES_LOWERCASE = TIMELINE_RESERVED_LOG_TYPES.map { it.lowercase() }.toSet()

private fun String.isReservedLogType(): Boolean = lowercase() in RESERVED_LOG_TYPES_LOWERCASE

private fun TimelineSummaryUiModel.toLogFilter(
    facets: Map<TimelineFacet, Set<String>>,
    logs: TimelineLogRules,
    tagQuery: String,
): TimelineLogFilterUiModel {
    val typeCounts = facetCounts[TimelineFacet.LogType].orEmpty()
    val hiddenTypes = facets[TimelineFacet.LogType].orEmpty()
    // the SDK sends the exact names, but the query treats the reserved types without case
    val reserved = TIMELINE_RESERVED_LOG_TYPES.map { type ->
        TimelineLogTypeUiModel(
            type = type,
            count = typeCounts.filterKeys { it.equals(type, ignoreCase = true) }.values.sum(),
            selected = type !in hiddenTypes,
        )
    }
    val tags = typeCounts.filterKeys { !it.isReservedLogType() }
    val tagged = TimelineLogTypeUiModel(type = null, count = tags.values.sum(), selected = logs.taggedEnabled)
    val types = reserved + tagged

    val existing = logs.tagRules.map { it.value.lowercase() }.toSet()
    val query = tagQuery.trim()
    val suggestions = tags
        .filterKeys { it.contains(query, ignoreCase = true) && it.lowercase() !in existing }
        .entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.key })
        .take(MAX_TAG_SUGGESTIONS)
        .map { TimelineFacetValueUiModel(value = it.key, count = it.value, selected = true, logType = it.key) }

    fun rules(exclude: Boolean) = logs.tagRules
        .filter { it.exclude == exclude }
        .map { TimelineRuleUiModel(it.value, it.exclude, it.active) }
        .toImmutableList()

    return TimelineLogFilterUiModel(
        types = types.toImmutableList(),
        allTypesSelected = types.all { it.selected },
        totalCount = types.sumOf { it.count },
        taggedEnabled = logs.taggedEnabled,
        tagQuery = tagQuery,
        tagSuggestions = suggestions.toImmutableList(),
        tagIncludes = rules(exclude = false),
        tagExcludes = rules(exclude = true),
        levels = toGroup(TimelineFacet.LogTaggedLevel, hidden = facets[TimelineFacet.LogTaggedLevel].orEmpty())?.values.orEmpty().toImmutableList(),
    )
}

private fun TimelineSummaryUiModel.toNetworkFilter(
    facets: Map<TimelineFacet, Set<String>>,
    network: TimelineNetworkRules,
    matchingNetworkCount: Int?,
): TimelineNetworkFilterUiModel {
    fun values(facet: TimelineFacet) = toGroup(facet, hidden = facets[facet].orEmpty())?.values.orEmpty()

    val total = sourceCounts[TimelineSource.Network] ?: 0
    val rules = network.hostRules
    val allHosts = values(TimelineFacet.NetworkHost)
    // the hosts matching the words typed, the others are not offered
    val hosts = allHosts.filter { it.value.matchesHostRules(rules) }
    val buckets = TIMELINE_DURATION_BUCKETS_MS
    val durationCounts = facetCounts[TimelineFacet.NetworkSpeed].orEmpty()
    val duration = network.duration
    val start = duration?.let { buckets.indexOf(it.minMs).coerceAtLeast(0) } ?: 0
    val end = duration?.maxMs?.let { buckets.indexOf(it).takeIf { index -> index >= 0 } } ?: buckets.size

    return TimelineNetworkFilterUiModel(
        enabled = network.enabled,
        count = 0,
        activeFacetCount = 0,
        isFiltered = network.ruleCount > 0 || TimelineFacet.entries.any { it.source == TimelineSource.Network && facets[it].orEmpty().isNotEmpty() },
        matchingCount = if (network.enabled) matchingNetworkCount ?: total else total,
        totalCount = total,
        methods = values(TimelineFacet.NetworkMethod).toImmutableList(),
        statusClasses = values(TimelineFacet.NetworkStatus).toImmutableList(),
        statusRules = network.statusRules.map { TimelineRuleUiModel(it.value, it.exclude) }.toImmutableList(),
        histogram = buckets.map { durationCounts[it.toString()] ?: 0 }.toImmutableList(),
        pendingCount = facetCounts[TimelineFacet.NetworkState]?.get("PENDING") ?: 0,
        durationStart = start,
        durationEnd = end,
        durationLabel = duration?.let { "${formatDuration(it.minMs)} - ${it.maxMs?.let(::formatDuration) ?: "∞"}" },
        content = values(TimelineFacet.NetworkContent).toImmutableList(),
        hosts = hosts.toImmutableList(),
        hostRules = rules.map { TimelineRuleUiModel(it.value, it.exclude) }.toImmutableList(),
        pathRules = network.pathRules.map { TimelineRuleUiModel(it.value, it.exclude) }.toImmutableList(),
        hiddenHostCount = allHosts.count { !it.selected },
        hostCount = hosts.size,
    )
}

// a host matches when it has one of the included words, if any, and none of the excluded ones
private fun String.matchesHostRules(rules: List<TimelineRuleDomainModel>): Boolean {
    val includes = rules.filter { !it.exclude }
    val excludes = rules.filter { it.exclude }
    return (includes.isEmpty() || includes.any { contains(it.value, ignoreCase = true) }) &&
        excludes.none { contains(it.value, ignoreCase = true) }
}

private fun formatDuration(ms: Long): String = if (ms >= 1_000 && ms % 1_000 == 0L) "${ms / 1_000} s" else "$ms ms"

// the methods of the facet are the ones of the timeline query: WS and GRPC for the calls that are not http
internal fun String.toMethodUi(): NetworkMethodUi = when (this) {
    "WS" -> NetworkMethodUi.WebSocket
    "GRPC" -> NetworkMethodUi.Grpc
    else -> toHttpMethodUi(this)
}

// every value is checked, so shown, unless it is one of the [hidden] values
private fun TimelineSummaryUiModel.toGroup(
    facet: TimelineFacet,
    hidden: Set<String>,
): TimelineFacetGroupUiModel? {
    val counts = facetCounts[facet].orEmpty()
    // the known values are always there, in their order, then the ones the events brought
    val others = (counts.keys + hidden - facet.knownValues.toSet()).sorted()
    val values = (facet.knownValues + others)
        .map { value ->
            TimelineFacetValueUiModel(
                value = value,
                count = counts[value] ?: 0,
                selected = value !in hidden,
                method = value.takeIf { facet == TimelineFacet.NetworkMethod }?.toMethodUi(),
                httpFamily = value.takeIf { facet == TimelineFacet.NetworkStatus }?.firstOrNull()?.digitToIntOrNull(),
                logLevel = value.takeIf { facet == TimelineFacet.LogLevel || facet == TimelineFacet.LogTaggedLevel }?.let(::logLevelOf),
                logType = value.takeIf { facet == TimelineFacet.LogType },
            )
        }
    return if (values.isEmpty()) {
        null
    } else {
        TimelineFacetGroupUiModel(
            facet = facet,
            values = values.toImmutableList(),
            asTags = facet == TimelineFacet.NetworkMethod || facet == TimelineFacet.NetworkStatus || facet == TimelineFacet.WebViewKind,
        )
    }
}
