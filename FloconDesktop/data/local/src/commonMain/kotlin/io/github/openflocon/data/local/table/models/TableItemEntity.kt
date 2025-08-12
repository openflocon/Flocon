package io.github.openflocon.data.local.table.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TableEntity::class,
            parentColumns = ["id"],
            childColumns = ["tableId"],
            onDelete = ForeignKey.Companion.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["tableId"]),
    ],
)
data class TableItemEntity(
    @PrimaryKey
    val itemId: String,
    val tableId: Long,
    val createdAt: Long,
    val columnsNames: List<String>,
    val values: List<String>,
)
