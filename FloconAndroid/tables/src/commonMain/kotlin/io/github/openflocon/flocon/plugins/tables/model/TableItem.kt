package io.github.openflocon.flocon.plugins.tables.model

import io.github.openflocon.flocon.pluginsold.tables.model.TableColumnConfig
import kotlinx.serialization.Serializable

data class TableItem(
    val id: String,
    val name: String,
    val createdAt: Long,
    val columns: List<TableColumnConfig>,
)

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
    columns = columns.map(TableColumnConfig::toRemote)
)

internal fun TableColumnConfig.toRemote(): TableColumnRemote = TableColumnRemote(
    column = columnName,
    value = value
)
