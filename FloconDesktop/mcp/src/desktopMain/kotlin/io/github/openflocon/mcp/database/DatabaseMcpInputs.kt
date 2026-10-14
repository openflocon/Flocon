package io.github.openflocon.mcp.database

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

internal const val DEFAULT_MAX_ROWS = 100
internal const val MAX_ROWS = 1_000
internal const val DEFAULT_MAX_CELL_CHARS = 1_000
internal const val MAX_CELL_CHARS = 100_000
internal const val DEFAULT_LOGS_LIMIT = 100
internal const val MAX_LOGS_LIMIT = 1_000

private const val DATABASE_ID_DESCRIPTION = "Id of the database, from list_databases."

@Serializable
internal data class ListDatabasesInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class ListDatabaseTablesInput(
    @McpDescription(DATABASE_ID_DESCRIPTION)
    val databaseId: String,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class QueryDatabaseInput(
    @McpDescription(DATABASE_ID_DESCRIPTION)
    val databaseId: String,
    @McpDescription("SQLite query, ex: \"SELECT * FROM users WHERE id = 3\".")
    val sql: String,
    @McpDescription("Maximum number of rows returned, from 1 to $MAX_ROWS. Default $DEFAULT_MAX_ROWS.")
    val maxRows: Int = DEFAULT_MAX_ROWS,
    @McpDescription("Values longer than this are truncated, from 10 to $MAX_CELL_CHARS. Default $DEFAULT_MAX_CELL_CHARS.")
    val maxCellChars: Int = DEFAULT_MAX_CELL_CHARS,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class ListDatabaseQueryLogsInput(
    @McpDescription(DATABASE_ID_DESCRIPTION)
    val databaseId: String,
    @McpDescription("Only the queries containing this text, in the SQL or the arguments.")
    val text: String? = null,
    @McpDescription("Include BEGIN / COMMIT / ROLLBACK statements. Default false.")
    val includeTransactions: Boolean = false,
    @McpDescription("Include queries from previous app launches. Default false.")
    val includeOldSessions: Boolean = false,
    @McpDescription("Maximum number of queries returned, from 1 to $MAX_LOGS_LIMIT. Default $DEFAULT_LOGS_LIMIT.")
    val limit: Int = DEFAULT_LOGS_LIMIT,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments
