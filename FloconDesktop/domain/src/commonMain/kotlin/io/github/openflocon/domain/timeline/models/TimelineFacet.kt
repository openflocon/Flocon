package io.github.openflocon.domain.timeline.models

// the kinds of SQL statement, TRANSACTION being BEGIN, COMMIT, END, ROLLBACK, SAVEPOINT and RELEASE, SCHEMA being CREATE, ALTER and DROP
private val DATABASE_STATEMENTS = listOf("SELECT", "INSERT", "UPDATE", "DELETE", "SCHEMA", "PRAGMA", "TRANSACTION", "OTHER")

/**
 * The start of each bucket of the duration histogram, in ms. A bucket ends where the next one starts, the last one is open.
 */
val TIMELINE_DURATION_BUCKETS_MS = listOf(0L, 50L, 100L, 200L, 300L, 500L, 750L, 1_000L, 2_000L, 5_000L)

/**
 * A way to narrow the events of one [source] down: every value is shown by default and the filter keeps the values
 * that are not hidden, ex: hiding the GET calls of the network.
 * [knownValues] are always offered, even before an event has them. The other values come from the events received.
 */
enum class TimelineFacet(
    val source: TimelineSource,
    val knownValues: List<String> = emptyList(),
) {
    NetworkMethod(
        source = TimelineSource.Network,
        knownValues = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS", "WS", "GRPC"),
    ),
    NetworkStatus(
        source = TimelineSource.Network,
        knownValues = listOf("1xx", "2xx", "3xx", "4xx", "5xx"),
    ),

    // PENDING: no response yet. OTHER: a response without http code, ex: gRPC
    NetworkState(
        source = TimelineSource.Network,
        knownValues = listOf("SUCCESS", "ERROR", "PENDING", "OTHER"),
    ),

    // the time the response took, nothing while waiting for it. A value is the start of a bucket of the histogram, in ms
    NetworkSpeed(
        source = TimelineSource.Network,
        knownValues = TIMELINE_DURATION_BUCKETS_MS.map { it.toString() },
    ),
    NetworkContent(
        source = TimelineSource.Network,
        knownValues = listOf("WEBSOCKET", "IMAGE", "OTHER"),
    ),
    NetworkHost(TimelineSource.Network),
    DatabaseName(TimelineSource.Database),
    DatabaseStatement(
        source = TimelineSource.Database,
        knownValues = DATABASE_STATEMENTS,
    ),
    AnalyticsTable(TimelineSource.Analytics),

    // the class of the exception, ex: java.lang.IllegalStateException
    CrashException(TimelineSource.Crash),
    AccessibilityAction(
        source = TimelineSource.Accessibility,
        knownValues = listOf(
            "ANNOUNCEMENT",
            "VIEW_CLICKED",
            "VIEW_FOCUSED",
            "VIEW_ACCESSIBILITY_FOCUSED",
            "VIEW_ACCESSIBILITY_FOCUS_CLEARED",
        ),
    ),
}
