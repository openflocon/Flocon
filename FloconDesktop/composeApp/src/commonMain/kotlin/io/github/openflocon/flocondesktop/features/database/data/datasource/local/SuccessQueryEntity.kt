package io.github.openflocon.flocondesktop.features.database.data.datasource.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName", "databaseId", "queryString"], unique = true),
        Index(value = ["databaseId"]),
    ],
)
data class SuccessQueryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val packageName: String,
    val databaseId: String,
    val queryString: String,
    val timestamp: Long,
)
