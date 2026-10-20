package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel

internal fun TimelineEventDomainModel.toUi(): TimelineRowUiModel = TimelineRowUiModel(
    id = id,
    source = source,
    time = formatTimestamp(timestamp),
    title = title,
    subtitle = subtitle?.takeIf { it.isNotBlank() },
)
