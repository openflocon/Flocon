package io.github.openflocon.flocondesktop.features.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSummaryUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelineSelectedSourcesUseCase
import io.github.openflocon.flocondesktop.features.timeline.mapper.toFilterPanel
import io.github.openflocon.flocondesktop.features.timeline.mapper.toUi
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterPanelUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class TimelineFilterPanelViewModel(
    observeTimelineSummaryUseCase: ObserveTimelineSummaryUseCase,
    private val setTimelineSelectedSourcesUseCase: SetTimelineSelectedSourcesUseCase,
    private val filterStore: TimelineFilterStore,
    dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    // the counts follow the text but not the facets: ticking a value must not make the others, or the sources, move or disappear
    private val summary: StateFlow<TimelineSummaryUiModel> = filterStore.filter
        .map { it.copy(facets = emptyMap()) }
        .distinctUntilChanged()
        .flatMapLatest { filter ->
            observeTimelineSummaryUseCase(filter).map { it.toUi(selected = filter.sources) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimelineSummaryUiModel.Empty)

    val panel: StateFlow<TimelineFilterPanelUiModel> = combine(
        summary,
        filterStore.storedSources,
        filterStore.facets,
    ) { summary, sources, facets ->
        summary.toFilterPanel(selectedSources = sources, facets = facets)
    }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimelineFilterPanelUiModel.Empty)

    fun onAction(action: TimelineFilterAction) {
        when (action) {
            is TimelineFilterAction.ToggleSource -> viewModelScope.launch {
                // read from the settings each time: a state flow nobody collects keeps its first value, the source could never come back
                val current = filterStore.storedSources.first()
                setTimelineSelectedSourcesUseCase(if (action.source in current) current - action.source else current + action.source)
            }

            is TimelineFilterAction.ToggleFacet -> filterStore.toggleFacet(action.facet, action.value)

            is TimelineFilterAction.ClearAll -> {
                filterStore.clear()
                viewModelScope.launch { setTimelineSelectedSourcesUseCase(TimelineSource.entries.toSet()) }
            }
        }
    }
}
