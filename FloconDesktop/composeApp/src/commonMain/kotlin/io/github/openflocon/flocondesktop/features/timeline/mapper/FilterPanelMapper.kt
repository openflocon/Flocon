package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFacetGroupUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFacetValueUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterPanelUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterSectionUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import kotlinx.collections.immutable.toImmutableList

/**
 * The sources with events come first. Every known value of a facet is listed, even without event.
 * With [onlyWithEvents], the sources and the facet values without event are hidden,
 * except the ones that are filtered on: they stay to be undone.
 * All the values are checked by default: [facets] holds the values the user unchecked.
 */
internal fun TimelineSummaryUiModel.toFilterPanel(
    selectedSources: Set<TimelineSource>,
    facets: Map<TimelineFacet, Set<String>>, // the values hidden, by facet
    onlyWithEvents: Boolean,
    currentSessionOnly: Boolean = false,
): TimelineFilterPanelUiModel {
    val sections = TimelineSource.entries
        .map { source ->
            val groups = TimelineFacet.entries
                .filter { it.source == source }
                .mapNotNull { facet -> toGroup(facet, hidden = facets[facet].orEmpty(), onlyWithEvents = onlyWithEvents) }
            TimelineFilterSectionUiModel(
                source = source,
                count = sourceCounts[source] ?: 0,
                enabled = source in selectedSources,
                activeFacetCount = TimelineFacet.entries.filter { it.source == source }.sumOf { facets[it].orEmpty().size },
                groups = groups.toImmutableList(),
            )
        }
        .sortedByDescending { it.count > 0 } // stable: the other of the sources is kept

    val hiddenSources = TimelineSource.entries.count { it !in selectedSources }
    val activeFilterCount = facets.values.sumOf { it.size } + hiddenSources + if (currentSessionOnly) 1 else 0

    return TimelineFilterPanelUiModel(
        sections = sections
            .filter { !onlyWithEvents || it.count > 0 || it.activeFacetCount > 0 }
            .toImmutableList(),
        onlyWithEvents = onlyWithEvents,
        currentSessionOnly = currentSessionOnly,
        allCount = sections.size,
        withEventsCount = sections.count { it.count > 0 },
        hasActiveFilters = activeFilterCount > 0,
        activeFilterCount = activeFilterCount,
    )
}

// every value is checked, so shown, unless it is one of the [hidden] values
private fun TimelineSummaryUiModel.toGroup(
    facet: TimelineFacet,
    hidden: Set<String>,
    onlyWithEvents: Boolean,
): TimelineFacetGroupUiModel? {
    val counts = facetCounts[facet].orEmpty()
    // the known values are always there, in their order, then the ones the events brought
    val others = (counts.keys + hidden - facet.knownValues.toSet()).sorted()
    val values = (facet.knownValues + others)
        .map { value -> TimelineFacetValueUiModel(value = value, count = counts[value] ?: 0, selected = value !in hidden) }
        .filter { !onlyWithEvents || it.count > 0 || !it.selected }
    return if (values.isEmpty()) {
        null
    } else {
        TimelineFacetGroupUiModel(facet = facet, values = values.toImmutableList())
    }
}
