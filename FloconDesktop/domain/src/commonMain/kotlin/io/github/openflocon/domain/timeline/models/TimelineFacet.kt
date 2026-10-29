package io.github.openflocon.domain.timeline.models

// the kinds of SQL statement, TRANSACTION being BEGIN, COMMIT, END, ROLLBACK, SAVEPOINT and RELEASE, SCHEMA being CREATE, ALTER and DROP
private val DATABASE_STATEMENTS = listOf("SELECT", "INSERT", "UPDATE", "DELETE", "SCHEMA", "PRAGMA", "TRANSACTION", "OTHER")

/**
 * A way to narrow the events of one [source] down to some values, ex: the GET and POST calls of the network.
 * Nothing selected for a facet means no restriction.
 * [knownValues] are always offered, even before an event has them. The other values come from the events received.
 */
enum class TimelineFacet(
    val source: TimelineSource,
    val knownValues: List<String> = emptyList(),
    // true: the values selected are hidden instead of kept
    val exclude: Boolean = false,
) {
    NetworkMethod(
        source = TimelineSource.Network,
        knownValues = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS", "WS", "GRPC"),
    ),
    NetworkStatus(
        source = TimelineSource.Network,
        knownValues = listOf("1xx", "2xx", "3xx", "4xx", "5xx"),
    ),
    DatabaseName(TimelineSource.Database),
    DatabaseStatement(
        source = TimelineSource.Database,
        knownValues = DATABASE_STATEMENTS,
    ),
    DatabaseHide(
        source = TimelineSource.Database,
        knownValues = listOf("TRANSACTION", "PRAGMA"),
        exclude = true,
    ),
    AnalyticsTable(TimelineSource.Analytics),
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
