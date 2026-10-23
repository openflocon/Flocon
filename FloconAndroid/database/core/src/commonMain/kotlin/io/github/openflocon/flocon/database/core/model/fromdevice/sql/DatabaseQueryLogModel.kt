package io.github.openflocon.flocon.database.core.model.fromdevice.sql

import kotlinx.serialization.Serializable

@Serializable
internal data class DatabaseQueryLogModel(
    val dbName: String,
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val timestamp: Long,
)
