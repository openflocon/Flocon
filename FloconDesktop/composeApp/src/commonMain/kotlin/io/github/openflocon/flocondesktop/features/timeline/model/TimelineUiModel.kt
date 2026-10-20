package io.github.openflocon.flocondesktop.features.timeline.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timeline.models.TimelineSource

@Immutable
internal data class TimelineRowUiModel(
    val id: String,
    val source: TimelineSource,
    val day: String, // dd/MM/yyyy, to group the events by day
    val time: String,
    val timestamp: Long, // to compute the time since the previous event
    val title: String,
    val subtitle: String?,
    val network: TimelineNetworkUiModel? = null, // network calls only
) {
    // the ids of two sources can be the same
    val key: String get() = "${source.name}:$id"
}

@Immutable
internal data class TimelineNetworkUiModel(
    val method: String,
    val host: String?,
    val path: String,
    val statusCode: Int?, // null while waiting for the response, or without http code
    val duration: String?,
)

@Immutable
internal sealed interface TimelineItemUiModel {
    val key: String

    data class Row(val row: TimelineRowUiModel) : TimelineItemUiModel {
        override val key: String get() = row.key
    }

    // displayed above the first event of each day
    data class DayHeader(val day: String, override val key: String) : TimelineItemUiModel
}

internal sealed interface TimelineAction {
    data class FilterTextChanged(val text: String) : TimelineAction
    data class ToggleSource(val source: TimelineSource) : TimelineAction
    data class Open(val row: TimelineRowUiModel) : TimelineAction
}
