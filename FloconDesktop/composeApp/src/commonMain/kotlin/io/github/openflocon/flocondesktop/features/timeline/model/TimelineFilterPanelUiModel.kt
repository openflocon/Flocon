package io.github.openflocon.flocondesktop.features.timeline.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.MethodFilterState
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
    // the methods, in the state of the method filter of the network screen, to display the same dropdown
    val methodFilter: MethodFilterState? = null,
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

@Immutable
internal data class TimelineFilterPanelUiModel(
    val sections: ImmutableList<TimelineFilterSectionUiModel>,
    val currentSessionOnly: Boolean,
    // something is filtered: used to offer to clear it
    val hasActiveFilters: Boolean,
    // facets selected, or sources hidden: the badge of the button opening the panel
    val activeFilterCount: Int,
) {
    companion object {
        val Empty = TimelineFilterPanelUiModel(
            sections = persistentListOf(),
            currentSessionOnly = false,
            hasActiveFilters = false,
            activeFilterCount = 0,
        )
    }
}
