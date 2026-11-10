package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.common.time.formatDate
import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.models.TimelineTransactionDomainModel
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotDomainModel
import io.github.openflocon.flocondesktop.common.ui.TransactionFolding
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineExcludeUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineNestedUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineNetworkUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineTransactionUiModel
import kotlinx.collections.immutable.toImmutableList

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
    logLevel = level,
    excludes = excludes(),
    // a transaction of one query is not worth a group: the query is a row like the others. Its BEGIN and COMMIT stay, to be hidden
    transaction = transaction?.takeUnless { it.queryCount <= 1 && !it.isControl }?.toUi(),
)

private fun TimelineTransactionDomainModel.toUi() = TimelineTransactionUiModel(
    id = id,
    queryCount = queryCount,
    durationLabel = formatElapsed((endTimestamp - startTimestamp).coerceAtLeast(0).toDouble()),
    endTimestamp = endTimestamp,
    isControl = isControl,
    nested = nested?.let { TimelineNestedUiModel(id = it.id, queryCount = it.queryCount, depth = it.depth, path = it.path) },
)

// the facets worth excluding an event by: not the duration bucket, the state or the content of a call
private val ExcludableFacets = setOf(
    TimelineFacet.NetworkMethod,
    TimelineFacet.NetworkStatus,
    TimelineFacet.NetworkHost,
    TimelineFacet.DatabaseName,
    TimelineFacet.DatabaseStatement,
    TimelineFacet.AnalyticsTable,
    TimelineFacet.TableName,
    TimelineFacet.WebViewKind,
    TimelineFacet.CrashException,
    TimelineFacet.AccessibilityAction,
)

internal fun TimelineEventDomainModel.excludes() = TimelineFacet.entries
    .filter { it in ExcludableFacets }
    .mapNotNull { facet -> facets[facet]?.takeIf { it.isNotBlank() }?.let { TimelineExcludeUiModel(facet, it) } }
    .toImmutableList()

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

// the events, the transactions and the screenshots belong to a day, the headers of the days do not
private fun TimelineItemUiModel?.dayOrNull(): String? = when (this) {
    is TimelineItemUiModel.Row -> row.day
    is TimelineItemUiModel.TransactionHeader -> day
    is TimelineItemUiModel.Screenshot -> day
    is TimelineItemUiModel.DayHeader, null -> null
}

// the time of an event, or of the transaction it opens
private fun TimelineItemUiModel?.timestampOrNull(): Long? = when (this) {
    is TimelineItemUiModel.Row -> row.timestamp
    is TimelineItemUiModel.TransactionHeader -> transaction.endTimestamp
    is TimelineItemUiModel.Screenshot, is TimelineItemUiModel.DayHeader, null -> null
}

/**
 * The header of a transaction goes above its first query when the list goes down from the newest, above its last one
 * (so after it in the list order) when the list is [inverted]. The queries of a transaction are the ones sharing its id:
 * a query of another transaction, or none, ends the group. [folding] tells which transactions show their queries.
 */
internal fun transactionHeaderBetween(
    before: TimelineItemUiModel?,
    after: TimelineItemUiModel?,
    inverted: Boolean,
    folding: TransactionFolding,
): TimelineItemUiModel.TransactionHeader? {
    val first = (if (inverted) before else after) as? TimelineItemUiModel.Row ?: return null
    val neighbor = (if (inverted) after else before) as? TimelineItemUiModel.Row
    val transaction = first.row.transaction?.takeIf { it.queryCount > 1 } ?: return null
    if (neighbor?.row?.transaction?.id == transaction.id) return null
    return TimelineItemUiModel.TransactionHeader(
        transaction = transaction,
        day = first.row.day,
        time = formatTimestamp(transaction.endTimestamp),
        databaseName = first.row.subtitle,
        isExpanded = folding.isExpanded(transaction.id),
        // a transaction can be interrupted by other events: each of its headers has its own key
        key = "transaction:${transaction.id}:${first.row.key}",
    )
}

/**
 * The header of a transaction nested in another one goes above its first query, like the one of the outermost transaction,
 * and only when it ran more than one query. It is a second pass after [transactionHeaderBetween]: both headers can be
 * needed between the same two queries, the one of the outermost transaction goes first.
 */
internal fun nestedHeaderBetween(
    before: TimelineItemUiModel?,
    after: TimelineItemUiModel?,
    inverted: Boolean,
    folding: TransactionFolding,
): TimelineItemUiModel.TransactionHeader? {
    val first = (if (inverted) before else after) as? TimelineItemUiModel.Row ?: return null
    val neighbor = (if (inverted) after else before) as? TimelineItemUiModel.Row
    val transaction = first.row.transaction ?: return null
    val nested = transaction.nested?.takeIf { it.hasHeader } ?: return null
    if (neighbor?.row?.transaction?.nested?.id == nested.id) return null
    return TimelineItemUiModel.TransactionHeader(
        transaction = transaction,
        day = first.row.day,
        time = formatTimestamp(first.row.timestamp),
        databaseName = first.row.subtitle,
        isExpanded = folding.isExpanded(nested.id),
        key = "nested:${nested.id}:${first.row.key}",
        nested = nested,
    )
}

/**
 * The queries of a transaction are shown under its header unless it is folded, or a transaction that holds them is. The
 * statements that open and close it (BEGIN, COMMIT...) are only told by the header.
 */
internal fun TimelineItemUiModel.isHiddenInTransaction(folding: TransactionFolding): Boolean = when (this) {
    is TimelineItemUiModel.Row -> {
        val transaction = row.transaction
        transaction != null &&
            (transaction.isControl || !folding.isExpanded(transaction.id) || transaction.nested?.path.orEmpty().any { !folding.isExpanded(it) })
    }

    // the header of a nested transaction is hidden by the transactions that hold it
    is TimelineItemUiModel.TransactionHeader ->
        nested != null &&
            (!folding.isExpanded(transaction.id) || nested.path.dropLast(1).any { !folding.isExpanded(it) })

    else -> false
}

/**
 * Where a line hangs in a transaction: the id of the outermost transaction and how far it is indented, null for what is not
 * part of one. The queries of a transaction hang from its header; a nested transaction has a header that hangs from the
 * transaction that holds it.
 */
internal fun TimelineItemUiModel?.branch(): Pair<String, Int>? = when (this) {
    is TimelineItemUiModel.Row -> row.transaction?.takeIf { !it.isControl }?.let { it.id to (it.nested?.level ?: 0) }
    is TimelineItemUiModel.TransactionHeader -> nested?.let { transaction.id to (it.depth - 1) }
    else -> null
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
        newerTimestamp = before.timestampOrNull(),
        olderTimestamp = after.timestampOrNull(),
    ) ?: return null
    return TimelineItemUiModel.Screenshot(
        timestamp = shot.timestamp,
        day = formatDate(shot.timestamp).substringBefore(' '),
        filePath = shot.filePath,
        key = "screenshot:${shot.timestamp}",
        time = formatTimestamp(shot.timestamp).substringBefore('.'),
    )
}
