package io.github.openflocon.data.local.database.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        // apps can log millions of queries: the logs are always read for one database of an app, ordered by time
        Index(value = ["deviceId", "packageName", "dbId", "timestamp"]),
        // the logs of a transaction are read together
        Index(value = ["deviceId", "packageName", "dbId", "transactionId"]),
    ],
)
data class DatabaseQueryLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dbId: String,
    // display only, the logs are always read by dbId
    val dbName: String,
    val sqlQuery: String,
    val bindArgs: String?,
    val timestamp: Long,
    val isTransaction: Boolean,
    val transactionId: String? = null,

    val deviceId: String,
    val packageName: String,
    val appInstance: Long,
)
