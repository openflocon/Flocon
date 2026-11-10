package io.github.openflocon.flocondesktop.features.timeline.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
internal data class TimelineLogDetailUiModel(
    val value: String,
    val type: String,
    val level: TimelineLogLevel,
    val time: String,
    val params: ImmutableList<Pair<String, String>>,
)

internal fun previewTimelineLogDetailUiModel() = TimelineLogDetailUiModel(
    value = "Payment declined",
    type = "checkout",
    level = TimelineLogLevel.Warning,
    time = "20/10/2026 12:00:00.000",
    params = persistentListOf("cartId" to "c-42", "reason" to "insufficient_funds"),
)
