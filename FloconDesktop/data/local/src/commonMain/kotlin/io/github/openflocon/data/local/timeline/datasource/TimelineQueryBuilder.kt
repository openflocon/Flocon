package io.github.openflocon.data.local.timeline.datasource

import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource

internal const val KIND_NETWORK = "network"
internal const val KIND_DATABASE = "database"
internal const val KIND_ANALYTICS = "analytics"
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
private const val NO_NETWORK_COLUMNS = "NULL AS method, NULL AS statusCode, NULL AS durationMs"

internal class TimelineQuery(
    val sql: String,
    val params: List<Any>,
)

private class Union(
    val sql: String?, // null: nothing to query
    val params: List<Any>,
)

/**
 * One SELECT per source, with the same columns (kind, id, ts, title, subtitle, appInstance, method, statusCode, durationMs),
 * merged by a UNION ALL. The app starts are not rows: they are the sessions, see [sessionsSql].
 */
private fun buildUnion(
    deviceId: String,
    packageName: String,
    filter: TimelineFilterDomainModel,
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
        if (text != null) {
            where.append(textColumns.joinToString(prefix = " AND (", separator = " OR ", postfix = ")") { "$it LIKE ?" })
            repeat(textColumns.size) { params.add(text) }
        }
        val selectQuery = "SELECT $select FROM $from $where"
        // a UNION ALL member cannot have its own ORDER BY / LIMIT: wrap it
        return if (orderLimit != null) "SELECT * FROM ($selectQuery $orderLimit)" else selectQuery
    }

    val branches = buildList {
        if (TimelineSource.Network in filter.sources) {
            add(
                branch(
                    select = "'$KIND_NETWORK' AS kind, callId AS id, request_startTime AS ts, " +
                        "request_url AS title, response_statusFormatted AS subtitle, appInstance AS appInstance, " +
                        "CASE type WHEN 'WEBSOCKET' THEN 'WS' WHEN 'GRPC' THEN 'GRPC' ELSE request_method END AS method, " +
                        "COALESCE(response_http_responseHttpCode, response_graphql_responseHttpCode) AS statusCode, " +
                        "response_durationMs AS durationMs",
                    from = "FloconNetworkCallEntity",
                    appInstanceColumn = "appInstance",
                    textColumns = listOf("request_url", "request_method"),
                )
            )
        }
        if (TimelineSource.Database in filter.sources) {
            add(
                branch(
                    select = "'$KIND_DATABASE' AS kind, CAST(id AS TEXT) AS id, timestamp AS ts, " +
                        "sqlQuery AS title, dbName AS subtitle, appInstance AS appInstance, $NO_NETWORK_COLUMNS",
                    from = "DatabaseQueryLogEntity",
                    appInstanceColumn = "appInstance",
                    textColumns = listOf("sqlQuery", "dbName"),
                    orderLimit = "ORDER BY timestamp DESC LIMIT $MAX_DATABASE_LOGS",
                )
            )
        }
        if (TimelineSource.Analytics in filter.sources) {
            add(
                branch(
                    select = "'$KIND_ANALYTICS' AS kind, itemId AS id, createdAt AS ts, " +
                        "eventName AS title, analyticsTableId AS subtitle, appInstance AS appInstance, $NO_NETWORK_COLUMNS",
                    from = "AnalyticsItemEntity",
                    appInstanceColumn = "appInstance",
                    textColumns = listOf("eventName", "analyticsTableId"),
                )
            )
        }
        if (TimelineSource.Crash in filter.sources) {
            add(
                branch(
                    select = "'$KIND_CRASH' AS kind, crashId AS id, timestamp AS ts, " +
                        "exceptionType AS title, exceptionMessage AS subtitle, NULL AS appInstance, $NO_NETWORK_COLUMNS",
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
                        "TableEntity.name AS title, TableItemEntity.`values` AS subtitle, NULL AS appInstance, $NO_NETWORK_COLUMNS",
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
                        "link AS title, label AS subtitle, NULL AS appInstance, $NO_NETWORK_COLUMNS",
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
                        "command AS title, output AS subtitle, NULL AS appInstance, $NO_NETWORK_COLUMNS",
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
private fun sessionsSql(deviceId: String, packageName: String, params: MutableList<Any>): String =
    SESSION_TABLES.joinToString(separator = " UNION ") { table ->
        params.add(deviceId)
        params.add(packageName)
        "SELECT appInstance FROM $table WHERE deviceId = ? AND packageName = ?"
    }

private const val EMPTY_ROWS =
    "SELECT '' AS kind, '' AS id, 0 AS ts, '' AS title, NULL AS subtitle, NULL AS appInstance, " +
        "NULL AS method, NULL AS statusCode, NULL AS durationMs, NULL AS sessionStart WHERE 0"

/**
 * The events of the selected sources sorted by time, newest first. Each one carries the start of its session
 * (the latest app start before it), to group the events by session.
 */
internal fun buildTimelineQuery(
    deviceId: String,
    packageName: String,
    filter: TimelineFilterDomainModel,
): TimelineQuery {
    val union = buildUnion(deviceId, packageName, filter)
    val unionSql = union.sql ?: return TimelineQuery(sql = EMPTY_ROWS, params = emptyList())

    val params = ArrayList<Any>()
    val sessions = sessionsSql(deviceId, packageName, params)
    params.addAll(union.params)

    return TimelineQuery(
        sql = "WITH sessions(appInstance) AS ($sessions) " +
            "SELECT u.kind, u.id, u.ts, u.title, u.subtitle, u.appInstance, u.method, u.statusCode, u.durationMs, " +
            "(SELECT MAX(appInstance) FROM sessions WHERE appInstance <= u.ts) AS sessionStart " +
            "FROM ($unionSql) u ORDER BY u.ts DESC, u.id DESC",
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
 * When the selected events happened: the time range of the events is cut in [bucketCount] slices and the
 * events of each kind are counted in each slice. The app starts are rows of their own kind, one per slice.
 * Counting in the query keeps the result small whatever the number of events.
 */
internal fun buildTimelineActivityQuery(
    deviceId: String,
    packageName: String,
    filter: TimelineFilterDomainModel,
    bucketCount: Int,
): TimelineQuery {
    val union = buildUnion(deviceId, packageName, filter)
    val unionSql = union.sql ?: return TimelineQuery(
        sql = "SELECT '' AS kind, 0 AS bucket, 0 AS count WHERE 0",
        params = emptyList(),
    )

    val params = ArrayList<Any>(union.params)
    val sessions = sessionsSql(deviceId, packageName, params)
    // the last slice is bucketCount - 1: (ts - first) is at most (last - first), below (last - first + 1)
    fun bucket(column: String) = "CAST(($column - firstTs) * $bucketCount / (lastTs - firstTs + 1) AS INTEGER)"

    return TimelineQuery(
        sql = "WITH events AS ($unionSql), bounds AS (SELECT MIN(ts) AS firstTs, MAX(ts) AS lastTs FROM events) " +
            "SELECT kind, ${bucket("ts")} AS bucket, COUNT(*) AS count FROM events, bounds GROUP BY kind, bucket " +
            "UNION ALL " +
            "SELECT '$KIND_APP_START' AS kind, ${bucket("appInstance")} AS bucket, COUNT(*) AS count " +
            "FROM ($sessions), bounds WHERE appInstance BETWEEN firstTs AND lastTs GROUP BY bucket",
        params = params,
    )
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
            "WHERE sessionStart IS NOT NULL GROUP BY sessionStart",
        params = events.params,
    )
}
