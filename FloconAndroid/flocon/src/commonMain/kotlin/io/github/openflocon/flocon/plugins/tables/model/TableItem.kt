package io.github.openflocon.flocon.plugins.tables.model

import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

internal fun tableItemListToJson(items: Collection<TableItem>): String {
    return FloconEncoder.json.encodeToString(items.map { it.toRemote() })
}

@Serializable
internal class TableItemRemote(
    val id: String,
    val name: String,
    val createdAt: Long,
    val columns: List<TableColumnRemote>,
)

@Serializable
internal class TableColumnRemote(
    val column: String,
    val value: String,
)

// --- Mapping ---

internal fun TableItem.toRemote(): TableItemRemote = TableItemRemote(
    id = id,
    name = name,
    createdAt = createdAt,
    columns = columns.map { it.toRemote() }
)

internal fun TableColumnConfig.toRemote(): TableColumnRemote = TableColumnRemote(
    column = columnName,
    value = value
)