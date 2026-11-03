package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.common.time.formatDate
import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotDomainModel
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
        durationMs = durationMs,
        httpActions = hasHttpActions(method),
        isImage = isImage,
    )
}

// the method of a gRPC call and of a WebSocket one is the name of their protocol
internal fun hasHttpActions(method: String?): Boolean = method != "GRPC" && method != "WS"

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
 *
 * When the list is [inverted] (the newest at the bottom), [before] is displayed below [after]: the header of a day
 * goes above its oldest event, so after the last event of the day in the list order.
 */
internal fun separatorBetween(
    before: TimelineItemUiModel?,
    after: TimelineItemUiModel?,
    inverted: Boolean = false,
): TimelineItemUiModel? {
    val previous = before.dayOrNull()
    val next = after.dayOrNull()
    // the first event of a day when the list goes down, its last event when it goes up
    val dayStart = if (inverted) before else after
    val day = dayStart.dayOrNull()

    return if (dayStart != null && day != null && previous != next) {
        TimelineItemUiModel.DayHeader(day = day, key = "day:$day:${dayStart.key}")
    } else {
        null
    }
}

// the events and the screenshots belong to a day, the headers do not
private fun TimelineItemUiModel?.dayOrNull(): String? = when (this) {
    is TimelineItemUiModel.Row -> row.day
    is TimelineItemUiModel.Screenshot -> day
    is TimelineItemUiModel.DayHeader, null -> null
}

/**
 * The latest screenshot taken between two events: [newerTimestamp] (excluded) and [olderTimestamp] (included),
 * null at the ends of the list: after the newest event, before the oldest one. With no event at all there is no marker.
 * [shots] are sorted from the oldest.
 */
internal fun screenshotBetween(
    shots: List<TimelineScreenshotDomainModel>,
    newerTimestamp: Long?,
    olderTimestamp: Long?,
): TimelineScreenshotDomainModel? {
    if (newerTimestamp == null && olderTimestamp == null) return null
    return shots.lastOrNull { shot ->
        (olderTimestamp == null || shot.timestamp >= olderTimestamp) && (newerTimestamp == null || shot.timestamp < newerTimestamp)
    }
}

// [before] is the newer item, whether the list is inverted or not: the data is always sorted from the newest
internal fun screenshotMarkerBetween(
    before: TimelineItemUiModel?,
    after: TimelineItemUiModel?,
    shots: List<TimelineScreenshotDomainModel>,
): TimelineItemUiModel.Screenshot? {
    val shot = screenshotBetween(
        shots = shots,
        newerTimestamp = (before as? TimelineItemUiModel.Row)?.row?.timestamp,
        olderTimestamp = (after as? TimelineItemUiModel.Row)?.row?.timestamp,
    ) ?: return null
    return TimelineItemUiModel.Screenshot(
        timestamp = shot.timestamp,
        day = formatDate(shot.timestamp).substringBefore(' '),
        filePath = shot.filePath,
        key = "screenshot:${shot.timestamp}",
        time = formatTimestamp(shot.timestamp).substringBefore('.'),
    )
}
