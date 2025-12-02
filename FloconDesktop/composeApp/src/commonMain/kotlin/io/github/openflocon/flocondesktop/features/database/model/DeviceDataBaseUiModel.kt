package io.github.openflocon.flocondesktop.features.database.model

import io.github.openflocon.domain.database.models.DeviceDataBaseId

data class DeviceDataBaseUiModel(
    val id: DeviceDataBaseId,
    val name: String,
    val isSelected: Boolean,
    val tables: List<TableUiModel>?,
)

data class TableUiModel(
    val name: String,
    val columns: List<ColumnUiModel>
) {
    data class ColumnUiModel(
        val name: String,
        val type: String,
        val isPrimaryKey: Boolean,
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
