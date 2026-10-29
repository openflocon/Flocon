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
 * The sources with events come first. With [onlyWithEvents], the sources and the facet values without event are hidden,
 * except the ones that are filtered on: they stay to be undone.
 */
internal fun TimelineSummaryUiModel.toFilterPanel(
    selectedSources: Set<TimelineSource>,
    facets: Map<TimelineFacet, Set<String>>,
    onlyWithEvents: Boolean,
): TimelineFilterPanelUiModel {
    val sections = TimelineSource.entries
        .map { source ->
            val groups = TimelineFacet.entries
                .filter { it.source == source }
                .mapNotNull { facet -> toGroup(facet, selected = facets[facet].orEmpty(), onlyWithEvents = onlyWithEvents) }
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
    val activeFilterCount = facets.values.sumOf { it.size } + hiddenSources

    return TimelineFilterPanelUiModel(
        sections = sections
            .filter { !onlyWithEvents || it.count > 0 || it.activeFacetCount > 0 }
            .toImmutableList(),
        onlyWithEvents = onlyWithEvents,
        allCount = sections.size,
        withEventsCount = sections.count { it.count > 0 },
        hasActiveFilters = activeFilterCount > 0,
        activeFilterCount = activeFilterCount,
    )
}

private fun TimelineSummaryUiModel.toGroup(
    facet: TimelineFacet,
    selected: Set<String>,
    onlyWithEvents: Boolean,
): TimelineFacetGroupUiModel? {
    val counts = facetCounts[facet].orEmpty()
    val values = (counts.keys + selected)
        .map { value -> TimelineFacetValueUiModel(value = value, count = counts[value] ?: 0, selected = value in selected) }
        .filter { !onlyWithEvents || it.count > 0 || it.selected }
        .sortedBy { it.value }
    return if (values.isEmpty()) {
        null
    } else {
        TimelineFacetGroupUiModel(facet = facet, values = values.toImmutableList())
    }
}
