package io.github.openflocon.flocon.plugins.database.model.fromdevice

import kotlinx.serialization.Serializable

@Serializable
internal data class DatabaseQueryLogModel(
    val dbName: String,
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val timestamp: Long,
)
