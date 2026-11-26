package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel

/** The columns described by the result of `PRAGMA table_info`. */
internal fun extractTableColumns(result: DatabaseExecuteSqlResponseDomainModel): List<DatabaseTableDomainModel.Column> = (result as? DatabaseExecuteSqlResponseDomainModel.Select)?.let {
    it.values.mapNotNull {
        // val id = it.getOrNull(0)
        val name = it.getOrNull(1)
        val type = it.getOrNull(2)
        val notnull = it.getOrNull(3)
        // val dflt_value = it.getOrNull(4)
        // 0 when not part of the primary key, else the position in the key, starting at 1
        val primaryKey = it.getOrNull(5)
        if (name != null && type != null) {
            DatabaseTableDomainModel.Column(
                name = name,
                type = type,
                nullable = notnull == "0",
                primaryKey = primaryKey != null && primaryKey != "0",
            )
        } else {
            null
        }
    }
} ?: emptyList()
