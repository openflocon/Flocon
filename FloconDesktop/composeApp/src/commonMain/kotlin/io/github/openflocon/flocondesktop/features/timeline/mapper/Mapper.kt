package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.common.time.formatDate
import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineNetworkUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel

internal fun TimelineEventDomainModel.toUi(): TimelineRowUiModel = TimelineRowUiModel(
    id = id,
    source = source,
    day = formatDate(timestamp).substringBefore(' '),
    time = formatTimestamp(timestamp),
    timestamp = timestamp,
    title = title,
    subtitle = subtitle?.takeIf { it.isNotBlank() },
    network = if (source == TimelineSource.Network) toNetworkUi() else null,
    sessionStart = sessionStart,
    phase = phase,
)

private fun TimelineEventDomainModel.toNetworkUi(): TimelineNetworkUiModel {
    val (host, path) = splitUrl(title)
    return TimelineNetworkUiModel(
        method = method.orEmpty(),
        host = host,
        path = path,
        statusCode = statusCode,
        duration = durationMs?.let(::formatElapsed),
    )
}

/**
 * "https://api.acme.io/v1/users?id=2" -> "api.acme.io" and "/v1/users?id=2"
 */
internal fun splitUrl(url: String): Pair<String?, String> {
    if ("://" !in url) return null to url
    val withoutScheme = url.substringAfter("://")
    val host = withoutScheme.substringBefore('/').takeIf { it.isNotEmpty() }
    val path = withoutScheme.substringAfter('/', missingDelimiterValue = "")
    return host to "/$path"
}

/**
 * The events are sorted from the newest, so the header of a day goes above its first event.
 * [before] is the item displayed above, [after] the one below.
 */
internal fun separatorBetween(
    before: TimelineItemUiModel?,
    after: TimelineItemUiModel?,
): TimelineItemUiModel? {
    val next = (after as? TimelineItemUiModel.Row)?.row ?: return null
    val previous = (before as? TimelineItemUiModel.Row)?.row

    return if (previous?.day != next.day) {
        TimelineItemUiModel.DayHeader(day = next.day, key = "day:${next.day}:${next.key}")
    } else {
        null
    }
}
