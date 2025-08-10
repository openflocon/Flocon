package io.github.openflocon.flocon.plugins.tables.model

data class TableColumnConfig(
    val columnName: String,
    val value: String,
)

infix fun String.toParam(value: String) = TableColumnConfig(
    columnName = this,
    value = value,
)