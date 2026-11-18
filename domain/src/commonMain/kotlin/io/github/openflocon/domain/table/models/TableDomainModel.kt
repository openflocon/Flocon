package io.github.openflocon.domain.table.models

data class TableDomainModel(
    val name: String,
    val columns: List<String>,
    val items: List<TableItem>,
) {
    data class TableItem(
        val itemId: String,
        val createdAt: Long,
        val values: List<String>,
    )
}
