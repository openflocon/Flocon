package io.github.openflocon.flocondesktop.features.table.data.datasource.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName", "name"], unique = true),
    ],
)
data class TableEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deviceId: String,
    val packageName: String,
    val name: String
)
