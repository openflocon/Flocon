package io.github.openflocon.data.local.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseQueryLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dbName: String,
    val sqlQuery: String,
    val bindArgs: String?,
    val timestamp: Long
)
