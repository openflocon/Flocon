package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.database.models.DatabaseRowEditTargetDomainModel
import io.github.openflocon.domain.database.utils.quoteSqlIdentifier
import io.github.openflocon.domain.database.utils.toSqlLiteral

/**
 * The query writing the values of [invoke] `edited` which differ from the `original` ones.
 * The row is found by its primary key, and the changed values must still be the original ones,
 * so that a change made by the app in the meantime is not overwritten.
 *
 * `null` when there is nothing to write, or when the row can't be identified.
 */
class BuildUpdateRowQueryUseCase {
    operator fun invoke(
        target: DatabaseRowEditTargetDomainModel,
        original: List<String?>,
        edited: List<String?>,
    ): String? {
        val columns = target.columns
        if (original.size != columns.size || edited.size != columns.size) return null

        val changed = columns.indices.filter { columns[it].affinity.isEditable && original[it] != edited[it] }
        if (changed.isEmpty()) return null

        val primaryKey = columns.indices.filter { columns[it].isPrimaryKey }
        // a primary key which is not an INTEGER one can hold several NULL
        if (primaryKey.isEmpty() || primaryKey.any { original[it] == null }) return null

        val assignments = changed.joinToString(", ") {
            "${quoteSqlIdentifier(columns[it].name)} = ${toSqlLiteral(edited[it])}"
        }
        val keyConditions = primaryKey.map {
            "${quoteSqlIdentifier(columns[it].name)} = ${toSqlLiteral(original[it])}"
        }
        val unchangedConditions = changed
            .filter { columns[it].affinity.isComparable && !columns[it].isPrimaryKey }
            .map { "${quoteSqlIdentifier(columns[it].name)} IS ${toSqlLiteral(original[it])}" }
        val conditions = (keyConditions + unchangedConditions).joinToString(" AND ")

        return "UPDATE ${quoteSqlIdentifier(target.tableName)} SET $assignments WHERE $conditions"
    }
}
