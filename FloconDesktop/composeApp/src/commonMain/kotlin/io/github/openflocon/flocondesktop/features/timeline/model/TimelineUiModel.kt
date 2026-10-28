package io.github.openflocon.flocondesktop.features.timeline.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timeline.models.TimelinePhase
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
    val sessionStart: Long? = null, // the start of the session of the event, null if none is known
    val phase: TimelinePhase = TimelinePhase.Request, // network calls only: the request and the response are two rows
) {
    // the ids of two sources can be the same. Shared by the request and the response of a network call,
    // to select and highlight them together
    val callKey: String get() = "${source.name}:$id"

    // unique in the list
    val key: String get() = if (phase == TimelinePhase.Request) callKey else "$callKey:${phase.name}"
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

@Immutable
internal data class TimelineSettingsUiModel(
    val autoScroll: Boolean,
    val invertList: Boolean,
    val pinPanel: Boolean,
)

internal fun previewTimelineSettingsUiModel() = TimelineSettingsUiModel(
    autoScroll = false,
    invertList = false,
    pinPanel = false,
)

internal sealed interface TimelineAction {
    data class FilterTextChanged(val text: String) : TimelineAction
    data class ToggleSource(val source: TimelineSource) : TimelineAction
    data class Open(val row: TimelineRowUiModel) : TimelineAction
    data class ToggleAutoScroll(val value: Boolean) : TimelineAction
    data class InvertList(val value: Boolean) : TimelineAction
    data class PinPanel(val value: Boolean) : TimelineAction
}
