package io.github.openflocon.flocon.tables.model

data class TableItem(
    val id: String,
    val name: String,
    val createdAt: Long,
    val columns: List<TableColumnConfig>,
)

data class TableColumnConfig(
    val columnName: String,
    val value: String,
)
