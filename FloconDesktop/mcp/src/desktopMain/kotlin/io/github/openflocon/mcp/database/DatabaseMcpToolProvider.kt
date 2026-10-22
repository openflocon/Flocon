package io.github.openflocon.mcp.database

import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.FilterQueryLogDomainModel
import io.github.openflocon.domain.database.models.toFullSql
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Instant

internal class DatabaseMcpToolProvider(
    private val databaseRepository: DatabaseRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "list_databases",
            title = "List databases",
            description = "Lists the SQLite databases of the mobile app (Room, SQLDelight...), with the databaseId used by the other database tools. " +
                "The app must be connected to Flocon.",
            annotations = readOnlyToolAnnotations,
            handler = ::listDatabases,
        )

        server.addTypedTool(
            name = "list_database_tables",
            title = "List database tables",
            description = "Returns the schema of a database of the mobile app: its tables with their columns, types and primary keys. " +
                "The app must be connected to Flocon.",
            annotations = readOnlyToolAnnotations,
            handler = ::listTables,
        )

        server.addTypedTool(
            name = "query_database",
            title = "Query database",
            description = "Executes a read only SQL query (SELECT, PRAGMA or EXPLAIN) on a database of the mobile app and returns the rows. " +
                "Statements modifying the database are refused. Add a LIMIT to large queries: the app sends all the rows. " +
                "The app must be connected to Flocon.",
            annotations = readOnlyToolAnnotations,
            handler = ::queryDatabase,
        )

        server.addTypedTool(
            name = "list_database_query_logs",
            title = "List database query logs",
            description = "Lists the SQL queries executed by the mobile app on a database, newest first, with their arguments. " +
                "Only available when the app logs its queries to Flocon (floconLogDatabaseQuery, ex: with a Room query callback).",
            annotations = readOnlyToolAnnotations,
            handler = ::listQueryLogs,
        )
    }

    private suspend fun listDatabases(input: ListDatabasesInput): DatabasesMcpResult {
        val target = targetResolver.resolve(input)

        // the app answers asynchronously, the known databases are returned if it doesn't answer in time
        databaseRepository.askForDeviceDatabases(target)
        val databases = withTimeoutOrNull(DATABASES_TIMEOUT_MS) {
            databaseRepository.observeDeviceDatabases(target).first { it.isNotEmpty() }
        } ?: databaseRepository.observeDeviceDatabases(target).first()

        return DatabasesMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            databases = databases.map { DatabaseMcpModel(databaseId = it.id, name = it.name) },
        )
    }

    private suspend fun listTables(input: ListDatabaseTablesInput): DatabaseTablesMcpResult {
        val target = targetResolver.resolve(input)
        val databaseId = input.databaseId

        val tableNames = execute(target, databaseId, TABLES_QUERY).values.mapNotNull { it.firstOrNull() }
        val tables = coroutineScope {
            tableNames.map { tableName ->
                async {
                    DatabaseTableMcpModel(
                        name = tableName,
                        columns = execute(target, databaseId, "PRAGMA table_info(${tableName.quoteIdentifier()})").toColumns(),
                    )
                }
            }.awaitAll()
        }

        return DatabaseTablesMcpResult(databaseId = databaseId, tables = tables)
    }

    private suspend fun queryDatabase(input: QueryDatabaseInput): DatabaseQueryMcpResult {
        val target = targetResolver.resolve(input)
        val databaseId = input.databaseId
        val sql = input.sql
        val maxRows = input.maxRows.coerceIn(1, MAX_ROWS)
        val maxCellChars = input.maxCellChars.coerceIn(10, MAX_CELL_CHARS)

        checkReadOnlySql(sql)
        val result = execute(target, databaseId, sql)

        val rows = result.values.take(maxRows)
        return DatabaseQueryMcpResult(
            databaseId = databaseId,
            columns = result.columns,
            totalRows = result.values.size,
            returned = rows.size,
            rows = rows.map { row -> row.map { it?.truncate(maxCellChars) } },
        )
    }

    private suspend fun listQueryLogs(input: ListDatabaseQueryLogsInput): DatabaseQueryLogsMcpResult {
        val target = targetResolver.resolve(input)
        val databaseId = input.databaseId
        val text = input.text?.takeIf { it.isNotBlank() }
        val includeOldSessions = input.includeOldSessions
        val limit = input.limit.coerceIn(1, MAX_LOGS_LIMIT)

        // the logs are stored by database name
        val databaseName = databaseRepository.getDatabaseById(databaseId)?.name ?: databaseId

        val showTransactions = input.includeTransactions
        val filters = listOfNotNull(text?.let { FilterQueryLogDomainModel(text = it, type = FilterQueryLogDomainModel.FilterType.INCLUDE) })
        val appInstance = target.appInstance.takeUnless { includeOldSessions }

        // apps can log millions of queries: filtered and limited by the database, without counting them
        val logs = databaseRepository.getLatestQueryLogs(
            dbName = databaseName,
            showTransactions = showTransactions,
            filters = filters,
            deviceIdAndPackageName = target,
            appInstance = appInstance,
            limit = limit + 1,
        )
        val returned = logs.take(limit)

        return DatabaseQueryLogsMcpResult(
            databaseId = databaseId,
            databaseName = databaseName,
            returned = returned.size,
            hasMore = logs.size > limit,
            queries = returned.map {
                DatabaseQueryLogMcpModel(
                    sql = it.toFullSql(),
                    timestamp = Instant.fromEpochMilliseconds(it.timestamp).toString(),
                    timestampMs = it.timestamp,
                    isTransaction = it.isTransaction,
                    sessionId = it.appInstance,
                    isCurrentSession = it.appInstance == target.appInstance,
                )
            },
        )
    }

    private suspend fun execute(
        target: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        sql: String,
    ): DatabaseExecuteSqlResponseDomainModel.Select {
        val response = databaseRepository.executeQuery(
            deviceIdAndPackageName = target,
            databaseId = databaseId,
            query = sql,
        ).fold(
            doOnFailure = {
                throw McpToolException(
                    "The app did not answer: check that it is running and connected to Flocon, " +
                        "and that '$databaseId' is a valid databaseId (see list_databases).",
                )
            },
            doOnSuccess = { it },
        )

        return when (response) {
            is DatabaseExecuteSqlResponseDomainModel.Select -> response

            is DatabaseExecuteSqlResponseDomainModel.Error -> throw McpToolException("SQL error: ${response.message}")

            // only read only queries are sent, they return rows
            else -> DatabaseExecuteSqlResponseDomainModel.Select(columns = emptyList(), values = emptyList())
        }
    }

    private companion object {
        const val DATABASES_TIMEOUT_MS = 3_000L

        // same as the Flocon database screen
        const val TABLES_QUERY = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' " +
            "AND name != 'android_metadata' AND name != 'room_master_table' ORDER BY name"
    }
}

/**
 * Rows of `PRAGMA table_info`: cid, name, type, notnull, dflt_value, pk
 */
private fun DatabaseExecuteSqlResponseDomainModel.Select.toColumns(): List<DatabaseColumnMcpModel> = values.mapNotNull { row ->
    val name = row.getOrNull(1) ?: return@mapNotNull null
    DatabaseColumnMcpModel(
        name = name,
        type = row.getOrNull(2).orEmpty(),
        nullable = row.getOrNull(3) == "0",
        primaryKey = row.getOrNull(5).let { it != null && it != "0" },
    )
}

private fun String.quoteIdentifier(): String = "\"" + replace("\"", "\"\"") + "\""

private fun String.truncate(maxChars: Int): String = if (length > maxChars) {
    take(maxChars) + "… [truncated, $length chars]"
} else {
    this
}
