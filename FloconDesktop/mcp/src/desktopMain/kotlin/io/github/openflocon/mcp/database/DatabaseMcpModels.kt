package io.github.openflocon.mcp.database

import kotlinx.serialization.Serializable

@Serializable
internal data class DatabasesMcpResult(
    val deviceId: String,
    val packageName: String,
    val databases: List<DatabaseMcpModel>,
)

@Serializable
internal data class DatabaseMcpModel(
    val databaseId: String,
    val name: String,
)

@Serializable
internal data class DatabaseTablesMcpResult(
    val databaseId: String,
    val tables: List<DatabaseTableMcpModel>,
)

@Serializable
internal data class DatabaseTableMcpModel(
    val name: String,
    val columns: List<DatabaseColumnMcpModel>,
)

@Serializable
internal data class DatabaseColumnMcpModel(
    val name: String,
    val type: String,
    val nullable: Boolean,
    val primaryKey: Boolean,
)

@Serializable
internal data class DatabaseQueryMcpResult(
    val databaseId: String,
    val columns: List<String>,
    val totalRows: Int,
    val returned: Int,
    val rows: List<List<String?>>,
)

@Serializable
internal data class DatabaseQueryLogsMcpResult(
    val databaseId: String,
    val databaseName: String,
    val returned: Int,
    val hasMore: Boolean,
    val queries: List<DatabaseQueryLogMcpModel>,
)

@Serializable
internal data class DatabaseQueryLogMcpModel(
    val sql: String,
    val timestamp: String,
    val timestampMs: Long,
    val isTransaction: Boolean,
    val sessionId: Long,
    val isCurrentSession: Boolean,
)
