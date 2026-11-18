package io.github.openflocon.domain.database.models

class DatabaseTableDomainModel(
    val tableName: String,
    val columns: List<Column>,
) {
    data class Column(
        val name: String,
        val type: String,
        val nullable: Boolean,
        val primaryKey: Boolean,
    )
}
