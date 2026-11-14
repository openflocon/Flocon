package io.github.openflocon.flocon.timeline.mapper

import io.github.openflocon.flocon.timeline.model.FloconTimelineLevel
import kotlinx.serialization.Serializable

@Serializable
internal class TimelineEventSerializable(
    val type: String,
    val value: String,
    val params: Map<String, String> = emptyMap(),
    val level: String? = null,
    val timestamp: Long,
)

internal fun FloconTimelineLevel.toRemote(): String = when (this) {
    FloconTimelineLevel.Debug -> "debug"
    FloconTimelineLevel.Info -> "info"
    FloconTimelineLevel.Warning -> "warning"
    FloconTimelineLevel.Error -> "error"
}
