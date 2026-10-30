package io.github.openflocon.flocondesktop.features.database.model

import io.github.openflocon.domain.database.models.DeviceDataBaseId
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class DeviceDataBaseUiModel(
    val id: DeviceDataBaseId,
    val name: String,
    val isSelected: Boolean,
    val tables: ImmutableList<TableUiModel>?,
)

data class TableUiModel(
    val name: String,
    val columns: ImmutableList<ColumnUiModel>
) {
    data class ColumnUiModel(
        val name: String,
        val type: String,
        val isPrimaryKey: Boolean,
        val isNotNull: Boolean = false,
    )
}

fun generateInsertQuery(table: TableUiModel): String {
    val columnNames = table.columns.joinToString(", ") { it.name }
    return buildString {
        appendLine("INSERT INTO ${ table.name }(")
        appendLine("\t" + columnNames)
        appendLine(") VALUES (")
        table.columns.forEachIndexed { index, it ->
            append("\t     /*${it.name}*/")
            if (index != table.columns.lastIndex) {
                append(",")
            }
            append("\n")
        }
        appendLine(")")
    }
}

fun previewDeviceDataBaseUiModel(id: String = "id") = DeviceDataBaseUiModel(
    id = id,
    name = "database.db",
    isSelected = false,
    tables = null,
)

fun List<DeviceDataBaseUiModel>.filterByQuery(query: String): List<DeviceDataBaseUiModel> {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return this
    return mapNotNull { database ->
        if (database.name.contains(trimmed, ignoreCase = true)) {
            database
        } else {
            val tables = database.tables?.filter { table ->
                table.name.contains(trimmed, ignoreCase = true) ||
                    table.columns.any { it.name.contains(trimmed, ignoreCase = true) }
            }
            if (tables.isNullOrEmpty()) null else database.copy(tables = tables.toImmutableList())
        }
    }
}
