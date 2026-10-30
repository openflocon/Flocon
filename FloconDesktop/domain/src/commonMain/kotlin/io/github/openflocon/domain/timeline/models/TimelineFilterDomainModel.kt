package io.github.openflocon.domain.timeline.models

import io.github.openflocon.domain.device.models.AppInstance

data class TimelineFilterDomainModel(
    val sources: Set<TimelineSource> = TimelineSource.entries.toSet(),
    val text: String? = null,
    val appInstance: AppInstance? = null, // null: every session
    // the values hidden by facet, a facet without value hides nothing. The facets of an unselected source are ignored
    val facets: Map<TimelineFacet, Set<String>> = emptyMap(),
)
