package io.github.openflocon.flocondesktop.features.timeline.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOption
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

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
    val logLevel: TimelineLogLevel? = null, // logs only
    val excludes: ImmutableList<TimelineExcludeUiModel> = persistentListOf(), // what the event can be excluded by
    val transaction: TimelineTransactionUiModel? = null, // database queries run in a transaction only
) {
    // the ids of two sources can be the same. Shared by the request and the response of a network call,
    // to select and highlight them together
    val callKey: String get() = "${source.name}:$id"

    // unique in the list
    val key: String get() = if (phase == TimelinePhase.Request) callKey else "$callKey:${phase.name}"
}

/**
 * The transaction of a database query. The queries of a transaction are grouped under one header.
 */
@Immutable
internal data class TimelineTransactionUiModel(
    val id: String,
    val queryCount: Int, // without the statements that open and close it (BEGIN, COMMIT...)
    val durationLabel: String,
    val endTimestamp: Long, // the time of the header
    val isControl: Boolean, // BEGIN, COMMIT...: only the header tells them
    val nested: TimelineNestedUiModel? = null, // the transaction nested in it the query runs in
)

/**
 * A transaction nested in another one (a savepoint). Its queries are grouped under a header of their own, unless it ran only one.
 */
@Immutable
internal data class TimelineNestedUiModel(
    val id: String,
    val queryCount: Int, // the queries it ran itself, without its statements
    val depth: Int, // 1 in a transaction nested in the outermost one, 2 in one nested in it...
    val path: List<String>, // the ids of the nested transactions that hold the query, outermost first, this one last
) {
    val hasHeader: Boolean get() = queryCount > 1

    /** How far the queries hang from the header of the transaction: a nested transaction without header does not indent them. */
    val level: Int get() = if (hasHeader) depth else (depth - 1).coerceAtLeast(0)
}

// a value of the event that can be hidden from the timeline, ex: the GET method
@Immutable
internal data class TimelineExcludeUiModel(
    val facet: TimelineFacet,
    val value: String,
)

@Immutable
internal data class TimelineNetworkUiModel(
    val method: String,
    val host: String?,
    val path: String,
    val statusCode: Int?, // null while waiting for the response, or without http code
    val duration: String?,
    val durationMs: Double? = null, // to color the slow calls
    // a gRPC or a WebSocket call has no http request to copy as cURL, replay or mock
    val httpActions: Boolean = true,
    val isImage: Boolean = false, // the response is an image
) {
    // the path alone, to exclude the calls of a route whatever their parameters
    val pathWithoutQuery: String get() = path.substringBefore('?')
}

@Immutable
internal sealed interface TimelineItemUiModel {
    val key: String

    data class Row(val row: TimelineRowUiModel) : TimelineItemUiModel {
        override val key: String get() = row.key
    }

    // displayed above the queries of a transaction, click to show or hide them
    data class TransactionHeader(
        val transaction: TimelineTransactionUiModel,
        val day: String, // like the rows, to place the headers of the days around it
        val time: String,
        val databaseName: String?,
        val isExpanded: Boolean,
        override val key: String,
        // set for the header of a transaction nested in [transaction], which has the id of the outermost one
        val nested: TimelineNestedUiModel? = null,
    ) : TimelineItemUiModel {
        // the id to fold or unfold to show or hide the queries under this header
        val foldingId: String get() = nested?.id ?: transaction.id
    }

    // displayed above the first event of each day
    data class DayHeader(val day: String, override val key: String) : TimelineItemUiModel

    // a screenshot of the app, between the events before and after the time it was taken
    data class Screenshot(
        val timestamp: Long,
        val day: String, // like the rows, to place the headers of the days around it
        val filePath: String,
        override val key: String,
        val time: String = "", // hh:mm:ss
    ) : TimelineItemUiModel
}

// the app and the device of the screenshots, shown beside them
@Immutable
internal data class TimelineScreenshotContextUiModel(
    val appName: String?,
    val appIconEncoded: String?,
    val deviceName: String,
    val platform: String, // Android, iOS...
)

// what the header says about the screenshots, next to the switch: the device does not support them (an Android over Wi-Fi)
internal enum class TimelineScreenshotsStatus { Working, Failing, Unavailable }

@Immutable
internal data class TimelineSettingsUiModel(
    val autoScroll: Boolean,
    val invertList: Boolean,
    val pinPanel: Boolean,
    val graph: Boolean = true,
    val mergePlugins: Boolean = false, // one graph for every type instead of one lane per type
    val screenshots: Boolean = false,
    val screenshotIntervalSeconds: Int = 5,
    val transactionsExpandedByDefault: Boolean = true, // a setting of the database transactions, shared with their logs screen
)

internal fun previewTimelineSettingsUiModel() = TimelineSettingsUiModel(
    autoScroll = false,
    invertList = false,
    pinPanel = false,
    graph = false,
    mergePlugins = false,
    screenshots = false,
    screenshotIntervalSeconds = 5,
)

internal sealed interface TimelineAction {
    data class FilterTextChanged(val text: String) : TimelineAction
    data object ToggleFilterPanel : TimelineAction
    data class Open(val row: TimelineRowUiModel) : TimelineAction

    // shows or hides the queries of a transaction
    data class ToggleTransaction(val id: String) : TimelineAction
    data class ToggleAutoScroll(val value: Boolean) : TimelineAction
    data class InvertList(val value: Boolean) : TimelineAction
    data class PinPanel(val value: Boolean) : TimelineAction
    data class TransactionsExpandedByDefault(val value: Boolean) : TimelineAction
    data class SetGraph(val value: Boolean) : TimelineAction
    data class SetMergePlugins(val value: Boolean) : TimelineAction
    data class SetScreenshots(val value: Boolean) : TimelineAction
    data class SetScreenshotInterval(val seconds: Int) : TimelineAction

    // the session of the graph: -1 the one before, 1 the one after
    data class MoveGraphSession(val delta: Int) : TimelineAction
    data class NetworkOption(val callId: String, val option: NetworkItemOption) : TimelineAction

    // hides a value of an event (ex: the GET calls) or its whole source, from the menu of the row
    data class Exclude(val facet: TimelineFacet, val value: String) : TimelineAction
    data class ExcludeSource(val source: TimelineSource) : TimelineAction
    data class ExcludePath(val path: String) : TimelineAction

    // opens the filter panel on the source, to manage what is excluded
    data class OpenFilters(val source: TimelineSource) : TimelineAction

    // copies the url of the network call selected, from the keyboard
    data object CopySelectedUrl : TimelineAction

    // the graph showed the zone it was asked to show: it is free again
    data object GraphFocusApplied : TimelineAction
}

// the actions of the filter panel
internal sealed interface TimelineFilterAction {
    data class ToggleSource(val source: TimelineSource) : TimelineFilterAction
    data class ToggleFacet(val facet: TimelineFacet, val value: String) : TimelineFilterAction
    data class SetCurrentSessionOnly(val value: Boolean) : TimelineFilterAction

    // the network card
    data class SetNetworkEnabled(val value: Boolean) : TimelineFilterAction

    // shows every network call again: the values hidden and the rules are cleared
    data object ResetNetwork : TimelineFilterAction

    // the values hidden of a facet at once: only one host (all the others hidden), none, or all of them
    data class SetHiddenValues(val facet: TimelineFacet, val values: Set<String>) : TimelineFilterAction

    // "404, 500-503": a code or a range, several of them separated by commas
    data class AddStatusRules(val text: String, val exclude: Boolean) : TimelineFilterAction
    data class RemoveStatusRule(val value: String) : TimelineFilterAction
    data class AddHostRule(val text: String, val exclude: Boolean) : TimelineFilterAction
    data class RemoveHostRule(val value: String) : TimelineFilterAction
    data class AddPathRule(val text: String, val exclude: Boolean) : TimelineFilterAction
    data class RemovePathRule(val value: String) : TimelineFilterAction

    // the database card: words of the sql query, and the transactions
    data class AddDatabaseRule(val text: String, val exclude: Boolean) : TimelineFilterAction
    data class RemoveDatabaseRule(val value: String) : TimelineFilterAction
    data class SetHideDatabaseTransactions(val value: Boolean) : TimelineFilterAction

    // positions of the slider, see TimelineNetworkFilterUiModel
    data class SetDurationRange(val start: Int, val end: Int) : TimelineFilterAction

    // the logs card: the reserved types and the levels use ToggleFacet(LogType | LogTaggedLevel)
    data class SetLogTaggedEnabled(val value: Boolean) : TimelineFilterAction
    data class AddLogTagRule(val text: String, val exclude: Boolean) : TimelineFilterAction
    data class SetLogTagRuleActive(val value: String, val active: Boolean) : TimelineFilterAction
    data class RemoveLogTagRule(val value: String) : TimelineFilterAction

    // what is typed in the tag field, to suggest the tags
    data class SetLogTagQuery(val text: String) : TimelineFilterAction
    data object ClearAll : TimelineFilterAction

    // the panel went to the source it was asked to show
    data object FocusHandled : TimelineFilterAction
}

internal fun previewTimelineRowUiModel(
    source: TimelineSource = TimelineSource.Network,
    id: String = "1",
    title: String = "title",
    subtitle: String? = null,
    network: TimelineNetworkUiModel? = null,
    phase: TimelinePhase = TimelinePhase.Request,
    logLevel: TimelineLogLevel? = null,
    excludes: ImmutableList<TimelineExcludeUiModel> = persistentListOf(),
    transaction: TimelineTransactionUiModel? = null,
) = TimelineRowUiModel(
    id = id,
    source = source,
    day = "20/10/2026",
    time = "12:00:00.000",
    timestamp = 0L,
    title = title,
    subtitle = subtitle,
    network = network,
    phase = phase,
    logLevel = logLevel,
    excludes = excludes,
    transaction = transaction,
)

internal fun previewTimelineTransaction(
    id: String = "tx-1",
    queryCount: Int = 2,
    isControl: Boolean = false,
    nested: TimelineNestedUiModel? = null,
) = TimelineTransactionUiModel(id = id, queryCount = queryCount, durationLabel = "5 ms", endTimestamp = 0L, isControl = isControl, nested = nested)

internal fun previewTimelineNested(id: String = "nested-1", queryCount: Int = 2, depth: Int = 1) = TimelineNestedUiModel(id = id, queryCount = queryCount, depth = depth, path = listOf(id))

internal fun previewTimelineNestedHeader(isExpanded: Boolean = true) = TimelineItemUiModel.TransactionHeader(
    transaction = previewTimelineTransaction(),
    day = "20/10/2026",
    time = "12:00:00.000",
    databaseName = "app.db",
    isExpanded = isExpanded,
    key = "nested:nested-1:preview",
    nested = previewTimelineNested(),
)

internal fun previewTimelineTransactionHeader(isExpanded: Boolean = true) = TimelineItemUiModel.TransactionHeader(
    transaction = previewTimelineTransaction(),
    day = "20/10/2026",
    time = "12:00:00.000",
    databaseName = "app.db",
    isExpanded = isExpanded,
    key = "transaction:tx-1:preview",
)

// one row of each kind of event
internal fun previewTimelineRows(): List<TimelineRowUiModel> = listOf(
    previewTimelineRowUiModel(
        source = TimelineSource.Network,
        id = "n1",
        title = "https://api.acme.io/v1/users?id=2",
        network = TimelineNetworkUiModel(method = "GET", host = "api.acme.io", path = "/v1/users?id=2", statusCode = null, duration = null),
        excludes = persistentListOf(
            TimelineExcludeUiModel(TimelineFacet.NetworkMethod, "GET"),
            TimelineExcludeUiModel(TimelineFacet.NetworkHost, "api.acme.io"),
        ),
    ),
    previewTimelineRowUiModel(
        source = TimelineSource.Network,
        id = "n1",
        title = "https://api.acme.io/v1/users?id=2",
        phase = TimelinePhase.Response,
        network = TimelineNetworkUiModel(method = "GET", host = "api.acme.io", path = "/v1/users?id=2", statusCode = 200, duration = "124 ms", durationMs = 124.0, isImage = true),
    ),
    previewTimelineRowUiModel(
        source = TimelineSource.Network,
        id = "n2",
        title = "https://api.acme.io/v1/orders",
        phase = TimelinePhase.Response,
        network = TimelineNetworkUiModel(method = "POST", host = "api.acme.io", path = "/v1/orders", statusCode = 500, duration = "3.4 s", durationMs = 3_400.0),
    ),
    previewTimelineRowUiModel(
        source = TimelineSource.Network,
        id = "n4",
        title = "https://api.acme.io/v1/users/42",
        phase = TimelinePhase.Response,
        network = TimelineNetworkUiModel(method = "GET", host = "api.acme.io", path = "/v1/users/42", statusCode = 404, duration = "87 ms", durationMs = 87.0),
    ),
    previewTimelineRowUiModel(
        source = TimelineSource.Network,
        id = "n3",
        title = "https://api.acme.io/acme.Users/List",
        subtitle = "OK",
        phase = TimelinePhase.Response,
        network = TimelineNetworkUiModel(method = "GRPC", host = "api.acme.io", path = "/acme.Users/List", statusCode = null, duration = "48 ms"),
    ),
    previewTimelineRowUiModel(
        source = TimelineSource.Database,
        id = "1",
        title = "SELECT id, name FROM users WHERE age > 18 AND name = 'bob' ORDER BY name LIMIT 20",
        subtitle = "app.db",
    ),
    previewTimelineRowUiModel(source = TimelineSource.Analytics, id = "a1", title = "screen_view", subtitle = "Firebase"),
    previewTimelineRowUiModel(source = TimelineSource.Accessibility, id = "ac1", title = "Pay now", subtitle = "Button"),
    previewTimelineRowUiModel(source = TimelineSource.WebView, id = "w1", title = "Uncaught TypeError: x is undefined", subtitle = "error"),
    previewTimelineRowUiModel(
        source = TimelineSource.Crash,
        id = "c1",
        title = "java.lang.IllegalStateException",
        subtitle = "Cannot read the cart before it is loaded",
    ),
    previewTimelineRowUiModel(source = TimelineSource.Table, id = "t1", title = "Cart", subtitle = "items=3, total=42.0"),
    previewTimelineRowUiModel(source = TimelineSource.Deeplink, id = "d1", title = "acme://product/42", subtitle = "Product"),
    previewTimelineRowUiModel(source = TimelineSource.Adb, id = "adb1", title = "adb shell pm list packages", subtitle = "package:com.acme.app"),
    previewTimelineRowUiModel(source = TimelineSource.Log, id = "1", title = "HomeScreen", subtitle = "navigation", logLevel = TimelineLogLevel.Info),
    previewTimelineRowUiModel(source = TimelineSource.Log, id = "2", title = "Tapped on Pay", subtitle = "userAction", logLevel = TimelineLogLevel.Debug),
    previewTimelineRowUiModel(source = TimelineSource.Log, id = "3", title = "App went to background", subtitle = "lifecycle", logLevel = TimelineLogLevel.Info),
    previewTimelineRowUiModel(source = TimelineSource.Log, id = "4", title = "Payment declined", subtitle = "checkout", logLevel = TimelineLogLevel.Warning),
    previewTimelineRowUiModel(source = TimelineSource.Log, id = "5", title = "Cart sync failed", subtitle = "checkout", logLevel = TimelineLogLevel.Error),
    previewTimelineRowUiModel(source = TimelineSource.AppStart, id = "s1"),
)

// a day of events, newest first, then the start of the session
internal fun previewTimelineItems(): List<TimelineItemUiModel> {
    val rows = previewTimelineRows()
    return buildList {
        add(TimelineItemUiModel.DayHeader(day = "20/10/2026", key = "day:20/10/2026"))
        rows.forEachIndexed { index, row ->
            add(TimelineItemUiModel.Row(row))
            // a screenshot every few events, to try its rail (the files do not exist: the thumbnails are empty boxes)
            if (index % PreviewScreenshotEvery == PreviewScreenshotEvery - 1 && index < rows.lastIndex) {
                add(previewTimelineScreenshot(timestamp = row.timestamp - 1, day = row.day, time = row.time.substringBefore('.')))
            }
        }
        add(previewTimelineTransactionHeader())
        add(
            TimelineItemUiModel.Row(
                previewTimelineRowUiModel(
                    source = TimelineSource.Database,
                    id = "tx1",
                    title = "UPDATE dog SET age = 7 WHERE id = 1",
                    subtitle = "app.db",
                    transaction = previewTimelineTransaction(),
                ),
            ),
        )
        add(
            TimelineItemUiModel.Row(
                previewTimelineRowUiModel(
                    source = TimelineSource.Database,
                    id = "tx2",
                    title = "INSERT INTO dog (name) VALUES ('Rex')",
                    subtitle = "app.db",
                    transaction = previewTimelineTransaction(),
                ),
            ),
        )
    }
}

private const val PreviewScreenshotEvery = 4

internal fun previewTimelineScreenshot(
    timestamp: Long = 0L,
    day: String = "20/10/2026",
    time: String = "12:00:30",
) = TimelineItemUiModel.Screenshot(
    timestamp = timestamp,
    day = day,
    filePath = "/missing/$timestamp.jpg",
    key = "screenshot:$timestamp",
    time = time,
)
