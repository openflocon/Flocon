package io.github.openflocon.flocondesktop.features.timeline.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timeline.models.TimelineSource

@Immutable
internal data class TimelineRowUiModel(
    val id: String,
    val source: TimelineSource,
    val day: String, // dd/MM/yyyy, to group the events by day
    val time: String,
    val title: String,
    val subtitle: String?,
)

@Immutable
internal sealed interface TimelineItemUiModel {
    data class Row(val row: TimelineRowUiModel) : TimelineItemUiModel

    // displayed above the first event of each day
    data class DayHeader(val day: String) : TimelineItemUiModel
}

internal sealed interface TimelineAction {
    data class FilterTextChanged(val text: String) : TimelineAction
    data class ToggleSource(val source: TimelineSource) : TimelineAction
    data class Open(val row: TimelineRowUiModel) : TimelineAction
}
