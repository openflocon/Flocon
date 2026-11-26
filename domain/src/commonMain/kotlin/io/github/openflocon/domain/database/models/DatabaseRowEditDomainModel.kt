package io.github.openflocon.domain.database.models

import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceId

/** Where the rows of a query result come from, when they can be updated. */
data class DatabaseRowEditTargetDomainModel(
    val deviceId: DeviceId,
    val packageName: AppPackageName,
    val databaseId: DeviceDataBaseId,
    val tableName: String,
    /** In the order of the columns of the query result. */
    val columns: List<Column>,
) {
    data class Column(
        val name: String,
        val isPrimaryKey: Boolean,
        val affinity: DatabaseColumnAffinity,
        /** `false` for a NOT NULL column. */
        val isNullable: Boolean,
    )
}

/** https://www.sqlite.org/datatype3.html#determination_of_column_affinity */
enum class DatabaseColumnAffinity(
    /** Blobs and untyped columns can't be written as text. */
    val isEditable: Boolean,
    /** The value read can be compared with the stored one, which is not the case of decimal numbers. */
    val isComparable: Boolean,
) {
    Integer(isEditable = true, isComparable = true),
    Text(isEditable = true, isComparable = true),
    Blob(isEditable = false, isComparable = false),
    Real(isEditable = true, isComparable = false),
    Numeric(isEditable = true, isComparable = false),
}

sealed interface DatabaseRowUpdateResultDomainModel {
    data object Updated : DatabaseRowUpdateResultDomainModel

    /** The row was deleted, or one of the edited values changed on the device since it was read. */
    data object RowNotFound : DatabaseRowUpdateResultDomainModel

    /** The selected device or app is not the one the row was read from. */
    data object DeviceChanged : DatabaseRowUpdateResultDomainModel

    /** A text was written for [column], which holds numbers. */
    data class InvalidNumber(val column: String) : DatabaseRowUpdateResultDomainModel

    data class Failed(val message: String) : DatabaseRowUpdateResultDomainModel
}
