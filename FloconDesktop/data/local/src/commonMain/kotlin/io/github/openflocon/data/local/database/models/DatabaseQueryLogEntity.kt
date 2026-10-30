package io.github.openflocon.data.local.database.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        // apps can log millions of queries: the logs are always read for one database of an app, ordered by time
        Index(value = ["deviceId", "packageName", "dbId", "timestamp"]),
    ],
)
data class DatabaseQueryLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dbId: String,
    val sqlQuery: String,
    val bindArgs: String?,
    val timestamp: Long,
    val isTransaction: Boolean,

    val deviceId: String,
    val packageName: String,
    val appInstance: Long,
)
