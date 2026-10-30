package io.github.openflocon.data.local.timeline.datasource

import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource

internal const val KIND_NETWORK = "network"
internal const val KIND_NETWORK_RESPONSE = "network_response" // the response of a network call, at the time it arrived
internal const val KIND_DATABASE = "database"
internal const val KIND_ANALYTICS = "analytics"
internal const val KIND_ACCESSIBILITY = "accessibility"
internal const val KIND_WEBVIEW = "webview"
internal const val KIND_CRASH = "crash"
internal const val KIND_TABLE = "table"
internal const val KIND_DEEPLINK = "deeplink"
internal const val KIND_APP_START = "appstart"
internal const val KIND_ADB = "adb"

// apps can log millions of queries: only the most recent ones are merged in the timeline
internal const val MAX_DATABASE_LOGS = 20_000

// the tables that store the session of their rows
private val SESSION_TABLES = listOf("FloconNetworkCallEntity", "DatabaseQueryLogEntity", "AnalyticsItemEntity")

// columns only the network calls fill
private const val NO_NETWORK_COLUMNS = "NULL AS method, NULL AS statusCode, NULL AS durationMs, 0 AS isImage"

// the values the facets filter on, one or two per source: the last columns of every member of the union
private const val NO_FACETS = "NULL AS facetA, NULL AS facetB"

private const val NETWORK_METHOD = "CASE type WHEN 'WEBSOCKET' THEN 'WS' WHEN 'GRPC' THEN 'GRPC' ELSE request_method END"
private const val NETWORK_STATUS_CODE = "COALESCE(response_http_responseHttpCode, response_graphql_responseHttpCode)"

// 1xx, 2xx..., null without http code
private const val NETWORK_STATUS_CLASS = "(CAST($NETWORK_STATUS_CODE AS INTEGER) / 100) || 'xx'"

// the kind of a SQL statement, from its first word. Same values as TimelineFacet.DatabaseStatement
private val DATABASE_STATEMENT = buildString {
    append("CASE")
    listOf(
        "SELECT" to listOf("SELECT", "WITH"),
        "INSERT" to listOf("INSERT", "REPLACE"),
        "UPDATE" to listOf("UPDATE"),
        "DELETE" to listOf("DELETE"),
        "SCHEMA" to listOf("CREATE", "ALTER", "DROP"),
        "PRAGMA" to listOf("PRAGMA"),
        "TRANSACTION" to listOf("BEGIN", "COMMIT", "END", "ROLLBACK", "SAVEPOINT", "RELEASE"),
    ).forEach { (statement, keywords) ->
        append(" WHEN ")
        append(keywords.joinToString(separator = " OR ") { "UPPER(LTRIM(sqlQuery)) LIKE '$it%'" })
        append(" THEN '$statement'")
    }
    append(" ELSE 'OTHER' END")
}

private const val FACET_A = "facetA"
private const val FACET_B = "facetB"

// the column of the union holding the value of a facet
private fun TimelineFacet.column(): String = when (this) {
    TimelineFacet.NetworkStatus,
    TimelineFacet.DatabaseStatement -> FACET_B

    else -> FACET_A
}

internal class TimelineQuery(
    val sql: String,
    val params: List<Any>,
)

private class Union(
    val sql: String?, // null: nothing to query
    val params: List<Any>,
)

/**
 * One SELECT per source, with the same columns (kind, id, ts, title, subtitle, appInstance, method, statusCode, durationMs, isImage),
 * merged by a UNION ALL. The app starts are not rows: they are the sessions, see [sessionsSql].
 */
private fun buildUnion(
    deviceId: String,
    packageName: String,
    filter: TimelineFilterDomainModel,
    includeNetworkResponses: Boolean = true, // false: one event per network call
    ignoredFacets: Set<TimelineFacet> = emptySet(), // facets not applied, to count their values
): Union {
    val params = ArrayList<Any>()
    val text = filter.text?.takeIf { it.isNotBlank() }?.let { "%$it%" }
    val appInstance = filter.appInstance

    fun branch(
        select: String,
        from: String,
        appInstanceColumn: String?, // null: the source does not store the session
        textColumns: List<String>,
        scopedByPackage: Boolean = true, // false: the source only stores the device
        orderLimit: String? = null,
        extraCondition: String? = null,
        facetExpressions: Map<TimelineFacet, String> = emptyMap(), // what each facet of the source filters on
    ): String {
        val where = StringBuilder("WHERE deviceId = ?")
        params.add(deviceId)
        if (extraCondition != null) {
            where.append(" AND $extraCondition")
        }
        if (scopedByPackage) {
            where.append(" AND packageName = ?")
            params.add(packageName)
        }
        if (appInstance != null && appInstanceColumn != null) {
            where.append(" AND $appInstanceColumn = ?")
            params.add(appInstance)
        }
        facetExpressions.forEach { (facet, expression) ->
            val values = filter.facets[facet]?.takeIf { it.isNotEmpty() && facet !in ignoredFacets }
            if (values != null) {
                // an event without value (ex: a call without http code yet) is not one of the values hidden
                where.append(" AND ($expression IS NULL OR $expression NOT IN (${values.joinToString { "?" }}))")
                params.addAll(values)
            }
        }
        if (text != null) {
            where.append(textColumns.joinToString(prefix = " AND (", separator = " OR ", postfix = ")") { "$it LIKE ?" })
            repeat(textColumns.size) { params.add(text) }
        }
        val selectQuery = "SELECT $select FROM $from $where"
        // a UNION ALL member cannot have its own ORDER BY / LIMIT: wrap it
        return if (orderLimit != null) "SELECT * FROM ($selectQuery $orderLimit)" else selectQuery
    }

    val networkFacets = mapOf(
        TimelineFacet.NetworkMethod to NETWORK_METHOD,
        TimelineFacet.NetworkStatus to NETWORK_STATUS_CLASS,
    )

    val branches = buildList {
        if (TimelineSource.Network in filter.sources) {
            add(
                branch(
                    select = "'$KIND_NETWORK' AS kind, callId AS id, request_startTime AS ts, " +
                        "request_url AS title, response_statusFormatted AS subtitle, appInstance AS appInstance, " +
                        "$NETWORK_METHOD AS method, " +
                        "$NETWORK_STATUS_CODE AS statusCode, " +
                        "response_durationMs AS durationMs, COALESCE(response_isImage, 0) AS isImage, " +
                        "$NETWORK_METHOD AS facetA, $NETWORK_STATUS_CLASS AS facetB",
                    from = "FloconNetworkCallEntity",
                    appInstanceColumn = "appInstance",
                    textColumns = listOf("request_url", "request_method"),
                    facetExpressions = networkFacets,
                )
            )
        }
        if (includeNetworkResponses && TimelineSource.Network in filter.sources) {
            add(
                branch(
                    select = "'$KIND_NETWORK_RESPONSE' AS kind, callId AS id, " +
                        "request_startTime + CAST(response_durationMs AS INTEGER) AS ts, " +
                        "request_url AS title, response_statusFormatted AS subtitle, appInstance AS appInstance, " +
                        "$NETWORK_METHOD AS method, " +
                        "$NETWORK_STATUS_CODE AS statusCode, " +
                        "response_durationMs AS durationMs, COALESCE(response_isImage, 0) AS isImage, " +
                        "$NETWORK_METHOD AS facetA, $NETWORK_STATUS_CLASS AS facetB",
                    from = "FloconNetworkCallEntity",
                    appInstanceColumn = "appInstance",
                    textColumns = listOf("request_url", "request_method"),
                    facetExpressions = networkFacets,
                    // a call without response yet has no response event
                    extraCondition = "response_durationMs IS NOT NULL",
                )
            )
        }
        if (TimelineSource.Database in filter.sources) {
            add(
                branch(
                    select = "'$KIND_DATABASE' AS kind, CAST(id AS TEXT) AS id, timestamp AS ts, " +
                        "sqlQuery AS title, dbName AS subtitle, appInstance AS appInstance, $NO_NETWORK_COLUMNS, " +
                        "dbName AS facetA, $DATABASE_STATEMENT AS facetB",
                    from = "DatabaseQueryLogEntity",
                    appInstanceColumn = "appInstance",
                    textColumns = listOf("sqlQuery", "dbName"),
                    facetExpressions = mapOf(
                        TimelineFacet.DatabaseName to "dbName",
                        TimelineFacet.DatabaseStatement to DATABASE_STATEMENT,
                    ),
                    orderLimit = "ORDER BY timestamp DESC LIMIT $MAX_DATABASE_LOGS",
                )
            )
        }
        if (TimelineSource.Analytics in filter.sources) {
            add(
                branch(
                    select = "'$KIND_ANALYTICS' AS kind, itemId AS id, createdAt AS ts, " +
                        "eventName AS title, analyticsTableId AS subtitle, appInstance AS appInstance, $NO_NETWORK_COLUMNS, " +
                        "analyticsTableId AS facetA, NULL AS facetB",
                    from = "AnalyticsItemEntity",
                    appInstanceColumn = "appInstance",
                    textColumns = listOf("eventName", "analyticsTableId"),
                    facetExpressions = mapOf(TimelineFacet.AnalyticsTable to "analyticsTableId"),
                )
            )
        }
        if (TimelineSource.Accessibility in filter.sources) {
            add(
                branch(
                    select = "'$KIND_ACCESSIBILITY' AS kind, id AS id, createdAt AS ts, " +
                        "COALESCE(text, contentDescription, type) AS title, type AS subtitle, appInstance AS appInstance, $NO_NETWORK_COLUMNS, " +
                        "type AS facetA, NULL AS facetB",
                    from = "AccessibilityEventEntity",
                    appInstanceColumn = "appInstance",
                    textColumns = listOf("text", "contentDescription"),
                    facetExpressions = mapOf(TimelineFacet.AccessibilityAction to "type"),
                )
            )
        }
        if (TimelineSource.WebView in filter.sources) {
            add(
                branch(
                    // a page event without message is told by its url, then by its type
                    select = "'$KIND_WEBVIEW' AS kind, eventId AS id, timestamp AS ts, " +
                        "COALESCE(message, url, label) AS title, label AS subtitle, NULL AS appInstance, $NO_NETWORK_COLUMNS, $NO_FACETS",
                    from = "WebViewEventEntity",
                    appInstanceColumn = null, // the events do not store the session
                    // the text displayed in the row, and the WebView
                    textColumns = listOf("COALESCE(message, url, label)", "webViewId"),
                )
            )
        }
        if (TimelineSource.Crash in filter.sources) {
            add(
                branch(
                    select = "'$KIND_CRASH' AS kind, crashId AS id, timestamp AS ts, " +
                        "exceptionType AS title, exceptionMessage AS subtitle, NULL AS appInstance, $NO_NETWORK_COLUMNS, $NO_FACETS",
                    from = "CrashReportEntity",
                    appInstanceColumn = null, // crashes do not store the session
                    textColumns = listOf("exceptionType", "exceptionMessage"),
                )
            )
        }
        if (TimelineSource.Table in filter.sources) {
            add(
                branch(
                    // the device and the package are stored by the table, not by its items
                    select = "'$KIND_TABLE' AS kind, TableItemEntity.itemId AS id, TableItemEntity.createdAt AS ts, " +
                        "TableEntity.name AS title, TableItemEntity.`values` AS subtitle, NULL AS appInstance, $NO_NETWORK_COLUMNS, $NO_FACETS",
                    from = "TableItemEntity JOIN TableEntity ON TableEntity.id = TableItemEntity.tableId",
                    appInstanceColumn = null,
                    textColumns = listOf("TableEntity.name", "TableItemEntity.`values`"),
                )
            )
        }
        if (TimelineSource.Deeplink in filter.sources) {
            add(
                branch(
                    select = "'$KIND_DEEPLINK' AS kind, CAST(id AS TEXT) AS id, executedAt AS ts, " +
                        "link AS title, label AS subtitle, NULL AS appInstance, $NO_NETWORK_COLUMNS, $NO_FACETS",
                    from = "DeeplinkEntity",
                    appInstanceColumn = null,
                    textColumns = listOf("link", "label"),
                    // the other rows are the deeplinks declared by the app, not events
                    extraCondition = "isHistory = 1",
                )
            )
        }
        if (TimelineSource.Adb in filter.sources) {
            add(
                branch(
                    select = "'$KIND_ADB' AS kind, CAST(id AS TEXT) AS id, executedAt AS ts, " +
                        "command AS title, output AS subtitle, NULL AS appInstance, $NO_NETWORK_COLUMNS, $NO_FACETS",
                    from = "AdbCommandHistoryEntity",
                    appInstanceColumn = null,
                    textColumns = listOf("command", "output"),
                    scopedByPackage = false, // adb commands are run on a device, not on an app
                )
            )
        }
    }

    return Union(
        sql = branches.takeIf { it.isNotEmpty() }?.joinToString(separator = " UNION ALL "),
        params = params,
    )
}

/**
 * The sessions are the app start times found in the tables that store them: there is no connection history.
 */
private fun sessionsSql(deviceId: String, packageName: String, params: MutableList<Any>): String = SESSION_TABLES.joinToString(separator = " UNION ") { table ->
    params.add(deviceId)
    params.add(packageName)
    "SELECT appInstance FROM $table WHERE deviceId = ? AND packageName = ?"
}

private const val EMPTY_ROWS =
    "SELECT '' AS kind, '' AS id, 0 AS ts, '' AS title, NULL AS subtitle, NULL AS appInstance, " +
        "NULL AS method, NULL AS statusCode, NULL AS durationMs, 0 AS isImage, NULL AS sessionStart WHERE 0"

/**
 * The events of the selected sources sorted by time, newest first. Each one carries the start of its session
 * (the latest app start before it), to group the events by session.
 * The app starts are rows of their own kind, at the time the app started, whether the session holds events or not.
 * They have no text, so they are hidden while the text filter is used.
 */
internal fun buildTimelineQuery(
    deviceId: String,
    packageName: String,
    filter: TimelineFilterDomainModel,
): TimelineQuery {
    val union = buildUnion(deviceId, packageName, filter)
    val showAppStarts = TimelineSource.AppStart in filter.sources && filter.text.isNullOrBlank()
    if (union.sql == null && !showAppStarts) return TimelineQuery(sql = EMPTY_ROWS, params = emptyList())

    val params = ArrayList<Any>()
    val sessions = sessionsSql(deviceId, packageName, params)
    val members = ArrayList<String>()
    union.sql?.let {
        members.add(it)
        params.addAll(union.params)
    }
    if (showAppStarts) {
        val appStartSessions = sessionsSql(deviceId, packageName, params)
        val appInstance = filter.appInstance
        val where = StringBuilder("WHERE appInstance IS NOT NULL")
        if (appInstance != null) {
            where.append(" AND appInstance = ?")
            params.add(appInstance)
        }
        members.add(
            "SELECT '$KIND_APP_START' AS kind, CAST(appInstance AS TEXT) AS id, appInstance AS ts, '' AS title, " +
                "NULL AS subtitle, appInstance AS appInstance, $NO_NETWORK_COLUMNS, $NO_FACETS FROM ($appStartSessions) $where"
        )
    }

    return TimelineQuery(
        sql = "WITH sessions(appInstance) AS ($sessions) " +
            "SELECT u.kind, u.id, u.ts, u.title, u.subtitle, u.appInstance, u.method, u.statusCode, u.durationMs, u.isImage, " +
            "(SELECT MAX(appInstance) FROM sessions WHERE appInstance <= u.ts) AS sessionStart " +
            "FROM (${members.joinToString(separator = " UNION ALL ")}) u " +
            // an app start is below the events that happened at the same time
            // at the same time, the response is above its request
            "ORDER BY u.ts DESC, (u.kind = '$KIND_APP_START') ASC, (u.kind = '$KIND_NETWORK_RESPONSE') DESC, u.id DESC",
        params = params,
    )
}

/**
 * Per kind: how many events, how many http errors and the time range. It follows the text of the filter
 * but not the selected sources, so every filter can show its count. The sessions are a kind of their own.
 */
internal fun buildTimelineSummaryQuery(
    deviceId: String,
    packageName: String,
    filter: TimelineFilterDomainModel,
): TimelineQuery {
    val union = buildUnion(
        deviceId = deviceId,
        packageName = packageName,
        filter = filter.copy(sources = TimelineSource.entries.toSet()),
        includeNetworkResponses = false,
    )
    val params = ArrayList<Any>()
    val members = ArrayList<String>()
    union.sql?.let {
        params.addAll(union.params)
        members.add(
            "SELECT kind, COUNT(*) AS count, " +
                "COALESCE(SUM(CASE WHEN statusCode >= 400 THEN 1 ELSE 0 END), 0) AS errors, " +
                "MIN(ts) AS firstTs, MAX(ts) AS lastTs FROM ($it) GROUP BY kind"
        )
    }
    val sessions = sessionsSql(deviceId, packageName, params)
    members.add(
        "SELECT '$KIND_APP_START' AS kind, COUNT(*) AS count, 0 AS errors, " +
            "MIN(appInstance) AS firstTs, MAX(appInstance) AS lastTs FROM ($sessions)"
    )
    return TimelineQuery(sql = members.joinToString(separator = " UNION ALL "), params = params)
}

/**
 * Per facet and value: how many events. It follows the text of the filter and the other facets, not the selected sources
 * nor the values of the facet itself, so every value can show its count and be ticked.
 */
internal fun buildTimelineFacetCountsQuery(
    deviceId: String,
    packageName: String,
    filter: TimelineFilterDomainModel,
): TimelineQuery {
    val params = ArrayList<Any>()
    val members = ArrayList<String>()
    TimelineFacet.entries.forEach { facet ->
        val union = buildUnion(
            deviceId = deviceId,
            packageName = packageName,
            filter = filter.copy(sources = setOf(facet.source)),
            includeNetworkResponses = false,
            ignoredFacets = setOf(facet),
        )
        union.sql?.let {
            params.addAll(union.params)
            val column = facet.column()
            members.add(
                "SELECT '${facet.name}' AS facet, $column AS value, COUNT(*) AS count FROM ($it) " +
                    "WHERE $column IS NOT NULL GROUP BY $column"
            )
        }
    }
    return TimelineQuery(sql = members.joinToString(separator = " UNION ALL "), params = params)
}

/**
 * How many of the selected events each session holds.
 */
internal fun buildTimelineSessionCountsQuery(
    deviceId: String,
    packageName: String,
    filter: TimelineFilterDomainModel,
): TimelineQuery {
    val events = buildTimelineQuery(deviceId, packageName, filter)
    return TimelineQuery(
        sql = "SELECT sessionStart, COUNT(*) AS count FROM (${events.sql}) " +
            // a session doesn't count its own app start
            "WHERE sessionStart IS NOT NULL AND kind <> '$KIND_APP_START' GROUP BY sessionStart",
        params = events.params,
    )
}
