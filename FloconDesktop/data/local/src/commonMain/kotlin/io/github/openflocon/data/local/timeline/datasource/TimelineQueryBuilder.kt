package io.github.openflocon.data.local.timeline.datasource

import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource

internal const val KIND_NETWORK = "network"
internal const val KIND_DATABASE = "database"
internal const val KIND_ANALYTICS = "analytics"
internal const val KIND_CRASH = "crash"
internal const val KIND_TABLE = "table"
internal const val KIND_DEEPLINK = "deeplink"
internal const val KIND_ADB = "adb"

// apps can log millions of queries: only the most recent ones are merged in the timeline
internal const val MAX_DATABASE_LOGS = 20_000

internal class TimelineQuery(
    val sql: String,
    val params: List<Any>,
)

/**
 * One SELECT per source, with the same columns (kind, id, ts, title, subtitle, appInstance),
 * merged by a UNION ALL and sorted by time, newest first.
 */
internal fun buildTimelineQuery(
    deviceId: String,
    packageName: String,
    filter: TimelineFilterDomainModel,
): TimelineQuery {
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
                        "request_method || ' ' || request_url AS title, response_statusFormatted AS subtitle, appInstance AS appInstance",
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
                        "sqlQuery AS title, dbName AS subtitle, appInstance AS appInstance",
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
                        "eventName AS title, analyticsTableId AS subtitle, appInstance AS appInstance",
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
                        "exceptionType AS title, exceptionMessage AS subtitle, NULL AS appInstance",
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
                        "TableEntity.name AS title, TableItemEntity.`values` AS subtitle, NULL AS appInstance",
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
                        "link AS title, label AS subtitle, NULL AS appInstance",
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
                        "command AS title, output AS subtitle, NULL AS appInstance",
                    from = "AdbCommandHistoryEntity",
                    appInstanceColumn = null,
                    textColumns = listOf("command", "output"),
                    scopedByPackage = false, // adb commands are run on a device, not on an app
                )
            )
        }
    }

    if (branches.isEmpty()) {
        return TimelineQuery(
            sql = "SELECT '' AS kind, '' AS id, 0 AS ts, '' AS title, NULL AS subtitle, NULL AS appInstance WHERE 0",
            params = emptyList(),
        )
    }

    return TimelineQuery(
        sql = branches.joinToString(separator = " UNION ALL ") + " ORDER BY ts DESC, id DESC",
        params = params,
    )
}
