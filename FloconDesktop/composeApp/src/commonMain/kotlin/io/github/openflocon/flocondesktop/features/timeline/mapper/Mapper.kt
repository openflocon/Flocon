package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.common.time.formatDate
import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel

internal fun TimelineEventDomainModel.toUi(): TimelineRowUiModel = TimelineRowUiModel(
    id = id,
    source = source,
    day = formatDate(timestamp).substringBefore(' '),
    time = formatTimestamp(timestamp),
    title = title,
    subtitle = subtitle?.takeIf { it.isNotBlank() },
)

/**
 * The events are sorted from the newest: a header is inserted above the first event of each day.
 * [before] is the item displayed above, [after] the one below.
 */
internal fun dayHeaderBetween(
    before: TimelineItemUiModel?,
    after: TimelineItemUiModel?,
): TimelineItemUiModel? {
    val next = (after as? TimelineItemUiModel.Row)?.row ?: return null
    val previous = (before as? TimelineItemUiModel.Row)?.row
    return if (previous?.day != next.day) TimelineItemUiModel.DayHeader(next.day) else null
}
