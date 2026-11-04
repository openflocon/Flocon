package io.github.openflocon.flocondesktop.features.timeline.graph

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineNetworkUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineRowUiModel

// the types drawn, from top to bottom. Database is the last one of the lanes: it is the one that spams the most, so it must not push the others down.
// A new lane goes between Network and Database. The app start is a line over the lanes, the other sources only appear in the list (the logs are instants)
internal enum class GraphLane(val source: TimelineSource) {
    Analytics(TimelineSource.Analytics),
    Network(TimelineSource.Network),
    Log(TimelineSource.Log),
    Database(TimelineSource.Database),
}

internal fun TimelineSource.toGraphLane(): GraphLane? = GraphLane.entries.firstOrNull { it.source == this }

/**
 * One bar (or one diamond when [durationMs] is 0) of the graph.
 * A network call is one event: the request and the response are merged.
 */
@Immutable
internal data class GraphEventUiModel(
    // the row opened in the detail panel when the event is selected
    val row: TimelineRowUiModel,
    val lane: GraphLane,
    val startMs: Long, // since the start of the session
    val durationMs: Long, // 0: instant
    val name: String,
    val method: String? = null,
    val statusCode: Int? = null,
    val isImage: Boolean = false,
    // the call failed without an http error: an exception, a cancellation…
    val hasError: Boolean = false,
    // the response did not arrive yet: the bar goes up to the last event received
    val inProgress: Boolean = false,
    // logs only: colors the marker
    val logLevel: TimelineLogLevel? = null,
) {
    val key: String get() = row.callKey
    val isInstant: Boolean get() = durationMs <= 0L
    val endMs: Long get() = startMs + durationMs

    // a http error, or a call that failed: with or without a status
    val isError: Boolean get() = hasError || (statusCode != null && statusCode >= 400)
}

// the events of one lane, in rows: the ones of a row never overlap, so they are sorted by start and by end
@Immutable
internal data class GraphLaneUiModel(
    val lane: GraphLane,
    val rows: List<List<GraphEventUiModel>>,
)

@Immutable
internal data class GraphUiModel(
    val lanes: List<GraphLaneUiModel>,
    val sessionDurationMs: Long,
    val eventCount: Int,
    val errorCount: Int,
    val slowest: GraphEventUiModel?,
    // the app start of the session is known: a vertical line at 0
    val hasAppStart: Boolean = false,
    // the time of day of the start of the session, to give a time to the axis
    val sessionStartTimestamp: Long = 0L,
    // the screenshots of the session, oldest first: the strip above the time axis
    val screenshots: List<GraphScreenshotUiModel> = emptyList(),
) {
    val rowCount: Int get() = lanes.sumOf { it.rows.size }

    companion object {
        val Empty = GraphUiModel(lanes = emptyList(), sessionDurationMs = 0L, eventCount = 0, errorCount = 0, slowest = null)
    }
}

// the 17 events of the reference session: 7 rows, 2 failed calls + 1 error log, 3 images (and the app start, a line)
internal fun previewGraphEvents(): List<GraphEventUiModel> {
    fun net(id: String, method: String, path: String, host: String, start: Long, duration: Long, status: Int, image: Boolean = false) = GraphEventUiModel(
        row = previewTimelineRowUiModel(
            source = TimelineSource.Network,
            id = id,
            title = "https://$host$path",
            phase = TimelinePhase.Request,
            network = TimelineNetworkUiModel(
                method = method,
                host = host,
                path = path,
                statusCode = status,
                duration = "$duration ms",
                durationMs = duration.toDouble(),
                isImage = image,
            ),
        ),
        lane = GraphLane.Network,
        startMs = start,
        durationMs = duration,
        name = path,
        method = method,
        statusCode = status,
        isImage = image,
    )

    fun db(id: String, query: String, start: Long) = GraphEventUiModel(
        row = previewTimelineRowUiModel(source = TimelineSource.Database, id = id, title = query, subtitle = "shop.sqlite"),
        lane = GraphLane.Database,
        startMs = start,
        durationMs = 0L,
        name = query,
    )

    fun analytics(id: String, name: String, start: Long) = GraphEventUiModel(
        row = previewTimelineRowUiModel(source = TimelineSource.Analytics, id = id, title = name, subtitle = "Firebase"),
        lane = GraphLane.Analytics,
        startMs = start,
        durationMs = 0L,
        name = name,
    )

    fun log(id: String, message: String, start: Long, level: TimelineLogLevel) = GraphEventUiModel(
        row = previewTimelineRowUiModel(source = TimelineSource.Log, id = id, title = message, logLevel = level),
        lane = GraphLane.Log,
        startMs = start,
        durationMs = 0L,
        name = message,
        hasError = level == TimelineLogLevel.Error,
        logLevel = level,
    )

    return listOf(
        net("1", "GET", "/v1/session", "api.shop.io", 0, 180, 200),
        analytics("2", "app_open", 20),
        net("3", "GET", "/v1/feed", "api.shop.io", 190, 640, 200),
        net("4", "GET", "/img/hero.jpg", "cdn.shop.io", 260, 520, 200, image = true),
        db("5", "SELECT * FROM products", 300),
        net("6", "GET", "/img/thumb_1.jpg", "cdn.shop.io", 420, 310, 200, image = true),
        net("7", "GET", "/img/thumb_2.jpg", "cdn.shop.io", 430, 780, 200, image = true),
        analytics("9", "screen_view", 860),
        net("10", "POST", "/v1/cart", "api.shop.io", 1320, 450, 201),
        db("11", "INSERT INTO cart_items", 1790),
        net("12", "GET", "/v1/recommendations", "api.shop.io", 1500, 1210, 504),
        analytics("13", "add_to_cart", 1800),
        net("14", "POST", "/v1/checkout", "api.shop.io", 2300, 520, 401),
        analytics("15", "checkout_failed", 2830),
        log("16", "Opened the feed screen", 250, TimelineLogLevel.Info),
        log("17", "Cart updated", 1810, TimelineLogLevel.Debug),
        log("18", "Checkout failed: 401", 2820, TimelineLogLevel.Error),
    )
}

internal const val PreviewSessionDurationMs = 3_000L
