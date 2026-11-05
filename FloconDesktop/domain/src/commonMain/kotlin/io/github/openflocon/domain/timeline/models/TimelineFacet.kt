package io.github.openflocon.domain.timeline.models

import io.github.openflocon.domain.timelinelog.models.TIMELINE_LOG_TYPE_LIFECYCLE
import io.github.openflocon.domain.timelinelog.models.TIMELINE_LOG_TYPE_NAVIGATION
import io.github.openflocon.domain.timelinelog.models.TIMELINE_LOG_TYPE_USER_ACTION

/**
 * The value of [TimelineFacet.DatabaseStatement] of what Room runs for itself: its invalidation tracker reads and writes
 * room_table_modification_log, in a transaction of its own after each write. A query of such a transaction is one of them.
 */
const val TIMELINE_DATABASE_STATEMENT_ROOM = "ROOM"

// the kinds of SQL statement, TRANSACTION being BEGIN, COMMIT, END, ROLLBACK, SAVEPOINT and RELEASE, SCHEMA being CREATE, ALTER and DROP
private val DATABASE_STATEMENTS = listOf("SELECT", "INSERT", "UPDATE", "DELETE", "SCHEMA", "PRAGMA", "TRANSACTION", TIMELINE_DATABASE_STATEMENT_ROOM, "OTHER")

/**
 * The values hidden until the user shows them again: the noise nobody asked for.
 */
// a getter, not a value: the facets use the values of this file when they are created, a value would be created with them
val TIMELINE_DEFAULT_HIDDEN_FACETS: Map<TimelineFacet, Set<String>>
    get() = mapOf(TimelineFacet.DatabaseStatement to setOf(TIMELINE_DATABASE_STATEMENT_ROOM))

// the types of log with their own look, they are not tagged logs: the tag and level filters never apply to them
val TIMELINE_RESERVED_LOG_TYPES = listOf(TIMELINE_LOG_TYPE_NAVIGATION, TIMELINE_LOG_TYPE_USER_ACTION, TIMELINE_LOG_TYPE_LIFECYCLE)

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

    // the type of the log: the known ones are always offered, the custom tags come from the logs received
    LogType(
        source = TimelineSource.Log,
        knownValues = TIMELINE_RESERVED_LOG_TYPES,
    ),

    // as stored: upper-case
    LogLevel(
        source = TimelineSource.Log,
        knownValues = listOf("DEBUG", "INFO", "WARNING", "ERROR"),
    ),

    // the level of the tagged logs only: the reserved types are stored INFO but have no level, they are never hidden by it
    LogTaggedLevel(
        source = TimelineSource.Log,
        knownValues = listOf("DEBUG", "INFO", "WARNING", "ERROR"),
    ),
}
