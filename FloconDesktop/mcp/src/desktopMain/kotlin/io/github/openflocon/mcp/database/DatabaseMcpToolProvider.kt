package io.github.openflocon.mcp.database

import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.FilterQueryLogDomainModel
import io.github.openflocon.domain.database.models.toFullSql
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.mcp.ARG_DEVICE_ID
import io.github.openflocon.mcp.ARG_PACKAGE_NAME
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.booleanArg
import io.github.openflocon.mcp.booleanProperty
import io.github.openflocon.mcp.intArg
import io.github.openflocon.mcp.integerProperty
import io.github.openflocon.mcp.jsonResult
import io.github.openflocon.mcp.properties
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.requiredStringArg
import io.github.openflocon.mcp.safeToolCall
import io.github.openflocon.mcp.stringArg
import io.github.openflocon.mcp.stringProperty
import io.github.openflocon.mcp.targetProperties
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonObjectBuilder
import kotlin.time.Instant

internal class DatabaseMcpToolProvider(
    private val databaseRepository: DatabaseRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: Server) {
        server.addTool(
            name = "list_databases",
            title = "List databases",
            description = "Lists the SQLite databases of the mobile app (Room, SQLDelight...), with the databaseId used by the other database tools. " +
                "The app must be connected to Flocon.",
            inputSchema = ToolSchema(
                properties = properties {
                    targetProperties()
                },
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { jsonResult(listDatabases(request)) }
        }

        server.addTool(
            name = "list_database_tables",
            title = "List database tables",
            description = "Returns the schema of a database of the mobile app: its tables with their columns, types and primary keys. " +
                "The app must be connected to Flocon.",
            inputSchema = ToolSchema(
                properties = properties {
                    databaseIdProperty()
                    targetProperties()
                },
                required = listOf(ARG_DATABASE_ID),
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { jsonResult(listTables(request)) }
        }

        server.addTool(
            name = "query_database",
            title = "Query database",
            description = "Executes a read only SQL query (SELECT, PRAGMA or EXPLAIN) on a database of the mobile app and returns the rows. " +
                "Statements modifying the database are refused. Add a LIMIT to large queries: the app sends all the rows. " +
                "The app must be connected to Flocon.",
            inputSchema = ToolSchema(
                properties = properties {
                    databaseIdProperty()
                    stringProperty(ARG_SQL, "SQLite query, ex: \"SELECT * FROM users WHERE id = 3\".")
                    integerProperty(
                        ARG_MAX_ROWS,
                        "Maximum number of rows returned. Default $DEFAULT_MAX_ROWS.",
                        minimum = 1,
                        maximum = MAX_ROWS.toLong(),
                    )
                    integerProperty(
                        ARG_MAX_CELL_CHARS,
                        "Values longer than this are truncated. Default $DEFAULT_MAX_CELL_CHARS.",
                        minimum = 10,
                        maximum = MAX_CELL_CHARS.toLong(),
                    )
                    targetProperties()
                },
                required = listOf(ARG_DATABASE_ID, ARG_SQL),
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { jsonResult(queryDatabase(request)) }
        }

        server.addTool(
            name = "list_database_query_logs",
            title = "List database query logs",
            description = "Lists the SQL queries executed by the mobile app on a database, newest first, with their arguments. " +
                "Only available when the app logs its queries to Flocon (floconLogDatabaseQuery, ex: with a Room query callback).",
            inputSchema = ToolSchema(
                properties = properties {
                    databaseIdProperty()
                    stringProperty(ARG_TEXT, "Only the queries containing this text, in the SQL or the arguments.")
                    booleanProperty(ARG_INCLUDE_TRANSACTIONS, "Include BEGIN / COMMIT / ROLLBACK statements. Default false.")
                    booleanProperty(ARG_INCLUDE_OLD_SESSIONS, "Include queries from previous app launches. Default false.")
                    integerProperty(
                        ARG_LIMIT,
                        "Maximum number of queries returned. Default $DEFAULT_LOGS_LIMIT.",
                        minimum = 1,
                        maximum = MAX_LOGS_LIMIT.toLong(),
                    )
                    targetProperties()
                },
                required = listOf(ARG_DATABASE_ID),
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { jsonResult(listQueryLogs(request)) }
        }
    }

    private suspend fun listDatabases(request: CallToolRequest): DatabasesMcpResult {
        val target = request.resolveTarget()

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

    private suspend fun listTables(request: CallToolRequest): DatabaseTablesMcpResult {
        val target = request.resolveTarget()
        val databaseId = request.requiredStringArg(ARG_DATABASE_ID)

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

    private suspend fun queryDatabase(request: CallToolRequest): DatabaseQueryMcpResult {
        val target = request.resolveTarget()
        val databaseId = request.requiredStringArg(ARG_DATABASE_ID)
        val sql = request.requiredStringArg(ARG_SQL)
        val maxRows = (request.intArg(ARG_MAX_ROWS) ?: DEFAULT_MAX_ROWS).coerceIn(1, MAX_ROWS)
        val maxCellChars = (request.intArg(ARG_MAX_CELL_CHARS) ?: DEFAULT_MAX_CELL_CHARS).coerceIn(10, MAX_CELL_CHARS)

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

    private suspend fun listQueryLogs(request: CallToolRequest): DatabaseQueryLogsMcpResult {
        val target = request.resolveTarget()
        val databaseId = request.requiredStringArg(ARG_DATABASE_ID)
        val text = request.stringArg(ARG_TEXT)
        val includeOldSessions = request.booleanArg(ARG_INCLUDE_OLD_SESSIONS) ?: false
        val limit = (request.intArg(ARG_LIMIT) ?: DEFAULT_LOGS_LIMIT).coerceIn(1, MAX_LOGS_LIMIT)

        // the logs are stored by database name
        val databaseName = databaseRepository.getDatabaseById(databaseId)?.name ?: databaseId

        val showTransactions = request.booleanArg(ARG_INCLUDE_TRANSACTIONS) ?: false
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

    private suspend fun CallToolRequest.resolveTarget(): DeviceIdAndPackageNameDomainModel = targetResolver.resolve(
        deviceId = stringArg(ARG_DEVICE_ID),
        packageName = stringArg(ARG_PACKAGE_NAME),
    )

    private fun JsonObjectBuilder.databaseIdProperty() {
        stringProperty(ARG_DATABASE_ID, "Id of the database, from list_databases.")
    }

    private companion object {
        const val ARG_DATABASE_ID = "databaseId"
        const val ARG_SQL = "sql"
        const val ARG_MAX_ROWS = "maxRows"
        const val ARG_MAX_CELL_CHARS = "maxCellChars"
        const val ARG_TEXT = "text"
        const val ARG_INCLUDE_TRANSACTIONS = "includeTransactions"
        const val ARG_INCLUDE_OLD_SESSIONS = "includeOldSessions"
        const val ARG_LIMIT = "limit"

        const val DEFAULT_MAX_ROWS = 100
        const val MAX_ROWS = 1_000
        const val DEFAULT_MAX_CELL_CHARS = 1_000
        const val MAX_CELL_CHARS = 100_000
        const val DEFAULT_LOGS_LIMIT = 100
        const val MAX_LOGS_LIMIT = 1_000

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
