package io.github.openflocon.flocon.plugins.tables.model

data class TableItem(
    val id: String,
    val name: String,
    val createdAt: Long,
    val columns: List<TableColumnConfig>,
)