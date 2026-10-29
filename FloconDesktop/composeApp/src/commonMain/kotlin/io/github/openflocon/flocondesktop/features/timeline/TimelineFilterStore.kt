package io.github.openflocon.flocondesktop.features.timeline

import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update

/**
 * The filter of the timeline, shared by the screen and by its filter panel: they are two destinations, so two view models.
 * The sources are a setting of the timeline, the text and the facets are not saved.
 */
@OptIn(FlowPreview::class)
internal class TimelineFilterStore(
    observeTimelineSettingsUseCase: ObserveTimelineSettingsUseCase,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val textState = MutableStateFlow("")
    val text: StateFlow<String> = textState.asStateFlow()

    // the values kept by facet
    private val facetsState = MutableStateFlow<Map<TimelineFacet, Set<String>>>(emptyMap())
    val facets: StateFlow<Map<TimelineFacet, Set<String>>> = facetsState.asStateFlow()

    // the filter panel only lists what has events
    private val onlyWithEventsState = MutableStateFlow(false)
    val onlyWithEvents: StateFlow<Boolean> = onlyWithEventsState.asStateFlow()

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
    ) { text, sources, facets ->
        TimelineFilterDomainModel(
            sources = sources,
            text = text,
            facets = facets,
        )
    }
        .shareIn(scope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    fun setText(text: String) {
        textState.update { text }
    }

    fun toggleFacet(facet: TimelineFacet, value: String) {
        facetsState.update { facets ->
            val values = facets[facet].orEmpty()
            val updated = if (value in values) values - value else values + value
            if (updated.isEmpty()) facets - facet else facets + (facet to updated)
        }
    }

    fun setOnlyWithEvents(value: Boolean) {
        onlyWithEventsState.update { value }
    }

    // the text and the facets, the sources are a setting
    fun clear() {
        textState.update { "" }
        facetsState.update { emptyMap() }
    }
}
