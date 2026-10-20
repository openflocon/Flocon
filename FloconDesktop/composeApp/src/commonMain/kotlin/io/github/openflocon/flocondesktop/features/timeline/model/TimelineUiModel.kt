package io.github.openflocon.flocondesktop.features.timeline.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timeline.models.TimelineSource

@Immutable
internal data class TimelineRowUiModel(
    val id: String,
    val source: TimelineSource,
    val time: String,
    val title: String,
    val subtitle: String?,
)

internal sealed interface TimelineAction {
    data class FilterTextChanged(val text: String) : TimelineAction
    data class ToggleSource(val source: TimelineSource) : TimelineAction
}
