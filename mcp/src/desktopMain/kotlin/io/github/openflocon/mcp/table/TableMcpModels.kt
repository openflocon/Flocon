package io.github.openflocon.mcp.table

import kotlinx.serialization.Serializable

@Serializable
internal data class TablesMcpResult(
    val deviceId: String,
    val packageName: String,
    val tables: List<TableMcpModel>,
)

@Serializable
internal data class TableMcpModel(
    val tableId: Long,
    val name: String,
)

@Serializable
internal data class TableItemsMcpResult(
    val deviceId: String,
    val packageName: String,
    val tableId: Long,
    val name: String,
    val columns: List<String>,
    val returned: Int,
    val hasMore: Boolean,
    val rows: List<TableRowMcpModel>,
)

@Serializable
internal data class TableRowMcpModel(
    val itemId: String,
    val timestamp: String,
    val timestampMs: Long,
    /** column name -> value */
    val values: Map<String, String>,
)
