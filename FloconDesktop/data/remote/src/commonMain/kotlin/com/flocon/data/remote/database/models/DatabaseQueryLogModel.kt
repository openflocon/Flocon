package com.flocon.data.remote.database.models

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseQueryLogModel(
    val dbName: String,
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val timestamp: Long,
)
